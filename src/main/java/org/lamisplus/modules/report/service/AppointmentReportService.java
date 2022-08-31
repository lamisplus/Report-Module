package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.entity.ARTClinical;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.domain.entity.HIVStatusTracker;
import org.lamisplus.modules.hiv.repositories.ARTClinicalRepository;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.HIVStatusTrackerRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.AppointmentMetaData;
import org.lamisplus.modules.report.domain.AppointmentReportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentReportService {

    private final ArtPharmacyRepository artPharmacyRepository;

    private final OrganisationUnitService organisationUnitService;

    private final ARTClinicalRepository artClinicalRepository;

    private final HIVStatusTrackerRepository hivStatusTrackerRepository;


    public Set<AppointmentReportDto> getMissRefillAppointment(Long facilityId, LocalDate start, LocalDate end) {
        List<Person> trackPatients = getTrackPatients();
        return artPharmacyRepository.findAll()
                .stream()
                .filter(getReportPeriod(facilityId, start, end))
                .filter(artPharmacy -> artPharmacy.getNextAppointment().isBefore(LocalDate.now()))
                .filter(artPharmacy -> trackPatients.contains(artPharmacy.getPerson()))
                .map(this::getAppointmentMetaData)
                .map(this::convertAppointmentMetaDataToAppointMentDto)
                .collect(Collectors.toSet());
    }

    @NotNull
    private Predicate<ArtPharmacy> getReportPeriod(Long facilityId, LocalDate start, LocalDate end) {
        return artPharmacy -> artPharmacy.getVisitDate() != null && isWithinDateRange(start, end, artPharmacy.getVisitDate()) && artPharmacy.getFacilityId().equals(facilityId);
    }


    public Set<AppointmentReportDto> getMissClinicAppointment(Long facilityId, LocalDate start, LocalDate end) {
        List<Person> trackPatients = getTrackPatients();
        return artClinicalRepository.findAll()
                .stream()
                .filter(clinical -> clinical.getVisitDate() != null && isWithinDateRange(start, end, clinical.getVisitDate()) && clinical.getFacilityId().equals(facilityId))
                .filter(artClinical -> artClinical.getIsCommencement() != null && !artClinical.getIsCommencement())
                .filter(clinical -> clinical.getNextAppointment().isBefore(LocalDate.now()))
                .filter(clinical -> trackPatients.contains(clinical.getPerson()))
                .map(this::getAppointmentMetaData)
                .map(this::convertAppointmentMetaDataToAppointMentDto)
                .collect(Collectors.toSet());
    }

    public Set<AppointmentReportDto> getRefillAppointment(Long facilityId, LocalDate start, LocalDate end) {
        List<Person> trackPatients = getTrackPatients();
        return artPharmacyRepository.findAll()
                .stream()
                .filter(artPharmacy -> artPharmacy.getNextAppointment() != null && isWithinDateRange(start, end, artPharmacy.getNextAppointment()) && artPharmacy.getFacilityId().equals(facilityId))
                // .filter (artPharmacy -> artPharmacy.getNextAppointment ().isBefore (LocalDate.now ()))
                .filter(artPharmacy -> trackPatients.contains(artPharmacy.getPerson()))
                .map(this::getAppointmentMetaData)
                .map(this::convertAppointmentMetaDataToAppointMentDto)
                .collect(Collectors.toSet());
    }

    public Set<AppointmentReportDto> getClinicAppointment(Long facilityId, LocalDate start, LocalDate end) {
        List<Person> trackPatients = getTrackPatients();
        return artClinicalRepository.findAll()
                .stream()
                .filter(clinical -> clinical.getNextAppointment() != null && isWithinDateRange(start, end, clinical.getNextAppointment()) && clinical.getFacilityId().equals(facilityId))
                .filter(artClinical -> artClinical.getIsCommencement() != null && !artClinical.getIsCommencement())
                //.filter (clinical -> clinical.getNextAppointment ().isBefore (LocalDate.now ()))
                .filter(clinical -> trackPatients.contains(clinical.getPerson()))
                .map(this::getAppointmentMetaData)
                .map(this::convertAppointmentMetaDataToAppointMentDto)
                .collect(Collectors.toSet());
    }


    private AppointmentMetaData getAppointmentMetaData(ARTClinical artClinical) {
        return AppointmentMetaData.builder()
                .nextAppointment(artClinical.getNextAppointment())
                .person(artClinical.getPerson())
                .facilityId(artClinical.getFacilityId())
                .visitDate(artClinical.getVisitDate())
                .build();
    }

    private AppointmentMetaData getAppointmentMetaData(ArtPharmacy artClinical) {
        return AppointmentMetaData.builder()
                .nextAppointment(artClinical.getNextAppointment())
                .person(artClinical.getPerson())
                .facilityId(artClinical.getFacilityId())
                .visitDate(artClinical.getVisitDate())
                .build();
    }


    private AppointmentReportDto convertAppointmentMetaDataToAppointMentDto(AppointmentMetaData metaData) {
        AppointmentReportDto appointmentReportDto = new AppointmentReportDto();
        appointmentReportDto.setDateOfLastVisit(metaData.getVisitDate());
        appointmentReportDto.setDateOfNextVisit(metaData.getNextAppointment());
        Long facilityId = metaData.getFacilityId();
        OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
        appointmentReportDto.setFacilityName(facility.getName());
        Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
        OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
        appointmentReportDto.setLga(lgaOrgUnitOfFacility.getName());
        Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
        OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
        appointmentReportDto.setState(state.getName());
        Person person = metaData.getPerson();
        if (person != null) {
            appointmentReportDto.setPatientId(person.getId());
            Optional<ARTClinical> artCommenceOptional = artClinicalRepository.findByPersonAndIsCommencementIsTrueAndArchived(person, 0);
            artCommenceOptional.ifPresent(artCommence -> appointmentReportDto.setArtStartDate(artCommence.getVisitDate()));
            appointmentReportDto.setHospitalNum(person.getHospitalNumber());
            String firstName = person.getFirstName();
            String otherName = person.getOtherName();
            String name = firstName + " " + otherName;
            appointmentReportDto.setName(name);
            LocalDate dateBirth = person.getDateOfBirth();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(dateBirth, currentDate).getYears();
            appointmentReportDto.setAge(age);
            appointmentReportDto.setDateBirth(dateBirth);
            appointmentReportDto.setSex(person.getSex());
            JsonNode address = person.getAddress();
            JsonNode address1 = address.get("address");
            Long stateOfResidenceId = null;
            Long lgaOfResidenceId = null;
            StringBuilder addressDetails = new StringBuilder();
            if (address1.isArray()) {
                JsonNode addressObject = address1.get(0);
                if (addressObject.hasNonNull("stateId") && addressObject.hasNonNull("district")) {
                    stateOfResidenceId = addressObject.get("stateId").asLong();
                    lgaOfResidenceId = addressObject.get("district").asLong();
                    addressDetails.append(addressObject.get("city").asText());
                    JsonNode town = addressObject.get("line");
                    if (!town.isNull() && town.isArray()) {
                        for (JsonNode node : town) {
                            addressDetails.append(" " + node.asText());
                        }
                    }
                }
            }
            OrganisationUnit stateOfResidency = organisationUnitService.getOrganizationUnit(stateOfResidenceId);
            appointmentReportDto.setStateOfResidence(stateOfResidency.getName());
            OrganisationUnit lgaOfResidency = organisationUnitService.getOrganizationUnit(lgaOfResidenceId);
            appointmentReportDto.setLgaOfResidence(lgaOfResidency.getName());
            JsonNode contactPoint = person.getContactPoint();
            StringBuilder phone = new StringBuilder();
            if (contactPoint.hasNonNull("contactPoint") && contactPoint.get("contactPoint").isArray()) {
                JsonNode phoneObject = contactPoint.get("contactPoint").get(1);
                String phoneValue = phoneObject.isNull() ? "" : phoneObject.get("value").asText();
                phone.append(phoneValue);
            }
            appointmentReportDto.setPhone(phone.toString());
            String firstChar = addressDetails.substring(0, 1).toUpperCase();
            String finalAddress = firstChar + addressDetails.substring(1).toLowerCase();
            appointmentReportDto.setAddress(finalAddress);
        }
        return appointmentReportDto;
    }

    private boolean isWithinDateRange(LocalDate start, LocalDate end, LocalDate checkDate) {
        return checkDate.equals(start) || checkDate.equals(end) || (checkDate.isAfter(start) && checkDate.isBefore(end));
    }

    @NotNull
    private List<Person> getTrackPatients() {
        List<String> status = Arrays.asList("Stopped Treatment", "Died (Confirmed)");
        return hivStatusTrackerRepository.findAll()
                .stream()
                .filter(hivStatusTracker -> !(status.contains(hivStatusTracker.getHivStatus())))
                .map(HIVStatusTracker::getPerson)
                .collect(Collectors.toList());
    }


}
