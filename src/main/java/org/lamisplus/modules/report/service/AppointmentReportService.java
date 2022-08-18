package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.entity.ARTClinical;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.domain.entity.HIVStatusTracker;
import org.lamisplus.modules.hiv.repositories.ARTClinicalRepository;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.HIVStatusTrackerRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.AppointmentReportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentReportService {

    private final ArtPharmacyRepository artPharmacyRepository;

    private final OrganisationUnitService organisationUnitService;

    private final ARTClinicalRepository artClinicalRepository;

    private  final HIVStatusTrackerRepository hivStatusTrackerRepository;


    public List<AppointmentReportDto> getMissRefillAppointment(Long facilityId, LocalDate start, LocalDate end) {
        List<String> status = Arrays.asList ("Stopped Treatment","Died (Confirmed)");
        List<Person> trackPatients = hivStatusTrackerRepository.findAll ()
                .stream ()
                .filter (hivStatusTracker -> ! (status.contains (hivStatusTracker.getHivStatus ())))
                .map (HIVStatusTracker::getPerson)
                .collect (Collectors.toList ());
        return artPharmacyRepository.findAll ()
                .stream ()
                .filter (artPharmacy -> isRefillWithinDateRange (start, end, artPharmacy) && artPharmacy.getFacilityId ().equals (facilityId))
                .filter (artPharmacy -> artPharmacy.getNextAppointment ().isBefore (LocalDate.now ()))
                .filter (artPharmacy -> trackPatients.contains (artPharmacy.getPerson ()))
                .map (this::convertRefillToAppointMentDto)
                .collect (Collectors.toList ());
    }

    private AppointmentReportDto convertRefillToAppointMentDto(ArtPharmacy artPharmacy) {
       AppointmentReportDto appointmentReportDto = new AppointmentReportDto ();
       appointmentReportDto.setDateOfLastVisit (artPharmacy.getVisitDate ());
       appointmentReportDto.setDateOfNextVisit (artPharmacy.getNextAppointment ());
        Long facilityId = artPharmacy.getFacilityId ();
        OrganisationUnit facility = organisationUnitService.getOrganizationUnit (facilityId);
        appointmentReportDto.setFacilityName (facility.getName ());
        Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId ();
        OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit (lgaIdOfTheFacility);
        appointmentReportDto.setLga (lgaOrgUnitOfFacility.getName ());
        Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId ();
        OrganisationUnit state = organisationUnitService.getOrganizationUnit (stateId);
        appointmentReportDto.setState (state.getName ());
        Person person = artPharmacy.getPerson ();
        Optional<ARTClinical> artCommenceOptional = artClinicalRepository.findByPersonAndIsCommencementIsTrueAndArchived (person, 0);
        artCommenceOptional.ifPresent (artCommence -> appointmentReportDto.setArtStartDate (artCommence.getVisitDate ()));
        appointmentReportDto.setHospitalNum (person.getHospitalNumber ());
        String name = person.getFirstName ().concat (" ").concat (person.getOtherName ());
        appointmentReportDto.setName (name);
        LocalDate dateBirth = person.getDateOfBirth ();
        LocalDate currentDate = LocalDate.now ();
        int age = Period.between (dateBirth, currentDate).getYears ();
        appointmentReportDto.setAge (age);
        appointmentReportDto.setDateBirth (dateBirth);
        appointmentReportDto.setSex (person.getSex ());
        JsonNode address = person.getAddress ();
        JsonNode address1 = address.get ("address");
        Long stateOfResidenceId = null;
        Long lgaOfResidenceId = null;
        StringBuilder addressDetails = new StringBuilder ();
        if (address1.isArray ()) {
            JsonNode addressObject = address1.get (0);
            if (addressObject.hasNonNull ("stateId") && addressObject.hasNonNull ("district")) {
                stateOfResidenceId = addressObject.get ("stateId").asLong ();
                lgaOfResidenceId = addressObject.get ("district").asLong ();
                addressDetails.append (addressObject.get ("city").asText ());
                JsonNode town = addressObject.get ("line");
                if (! town.isNull () && town.isArray ()) {
                    for (JsonNode node : town) {
                        addressDetails.append (" " + node.asText ());
                    }
                    //
                }
            }
        }
        OrganisationUnit stateOfResidency = organisationUnitService.getOrganizationUnit (stateOfResidenceId);
        appointmentReportDto.setStateOfResidence (stateOfResidency.getName ());
        OrganisationUnit lgaOfResidency = organisationUnitService.getOrganizationUnit (lgaOfResidenceId);
        appointmentReportDto.setLgaOfResidence (lgaOfResidency.getName ());
        JsonNode contactPoint = person.getContactPoint ();
        StringBuilder phone = new StringBuilder ();
        if (contactPoint.hasNonNull ("contactPoint") && contactPoint.get ("contactPoint").isArray ()) {
            JsonNode phoneObject = contactPoint.get ("contactPoint").get (1);
            String phoneValue = phoneObject.isNull () ? "" : phoneObject.get ("value").asText ();
            phone.append (phoneValue);
        }
        appointmentReportDto.setPhone (phone.toString ());
        String firstChar = addressDetails.substring (0, 1).toUpperCase ();
        String finalAddress = firstChar + addressDetails.substring (1).toLowerCase ();
        appointmentReportDto.setAddress (finalAddress);
        return appointmentReportDto;
    }

    private boolean isRefillWithinDateRange(LocalDate start, LocalDate end, ArtPharmacy artPharmacy) {
        LocalDate visitDate = artPharmacy.getVisitDate ();
        return visitDate.equals (start) || visitDate.equals (end) || (visitDate.isAfter (start) && visitDate.isBefore (end));
    }


}
