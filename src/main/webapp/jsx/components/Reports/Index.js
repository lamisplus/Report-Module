import React, {useMemo, useState} from 'react';
import { Card, CardBody } from 'reactstrap';
import { makeStyles } from '@material-ui/core/styles';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import 'react-widgets/dist/css/react-widgets.css';
import 'react-phone-input-2/lib/style.css';
import { Menu } from 'semantic-ui-react';
import 'semantic-ui-css/semantic.min.css';
import PatientLineList from './PatientLineList';
import Appointment from './Appointment';
import Radet from './Radet';
import BiometricReport from './BiometricReport';
import PharmacyReport from './PharmacyReport';
import LaboratoryReport from './LaboratoryReport';
import HTSReport from './HTSReport';
import HtsRegister from './HtsRegister';
import PrepReport from './PrepReport';
import ClinicData from './ClinicData';
import ClientVerification from './ClientVerification';
import TbReport from './TbReport';
import IndexElicitation from './IndexElicitation';
import PmtctHtsReport from './PmtctHtsReport';
import PmtctMaternalCohortReport from './PmtctMaternalCohortReport';
import NcdReport from './NcdReport';
import EACReport from './EACReport';
import AhdReport from './AhdReport';
import PrepLongitudinalReport from './PrepLongitudinalReport';
import MhpssReport from './MhpssReport';
import KpPrevReport from './KpPrevReport';
import HIVST from './HIVSTReport';
import HTSIndexReport from './HTSIndexReport';
import CustomReport from './CustomReport';
import ADRReport from './ADRReport';
import PMTCTMonthlySummaryReport from './PMTCTMonthlySummaryReport';
import 'react-toastify/dist/ReactToastify.css';
import 'react-widgets/dist/css/react-widgets.css';
import 'react-phone-input-2/lib/style.css';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Divider from '@mui/material/Divider';
import PrEPMSF from './PrEPMSF';
import HTS_MSF from './HTS_MSF';
import TbReportLongitudinal from './TbReportLongitudinal';
import { useRoles } from "../../../hooks/useRoles";
import { usePermissions } from "../../../hooks/usePermissions";
import NoAccessCard from "../Shared/NoAccessCard";

const useStyles = makeStyles(theme => ({
  error: {
    color: '#f85032',
    fontSize: '12.8px',
  },
  success: {
    color: '#4BB543 ',
    fontSize: '11px',
  },
}));

const Reports = props => {
  const classes = useStyles();
  const [activeItem, setactiveItem] = useState('basic');
  const [activeItem1, setActiveItem1] = useState('basic');
  const [expanded, setExpanded] = React.useState(false);
  const { hasRole, loading: rolesLoading } = useRoles();
  const { hasPermission, hasAnyPermission  }  = usePermissions();

  const [completed, setCompleted] = useState([]);
  const handleItemClick = activeItem => {
    setactiveItem(activeItem);
  };

  const handleChange = panel => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false);
  };

  const handleItemClick1 = value => {
    setActiveItem1(value);
  };

  const reportSurveillance = [
    {
      key: 'hts-report',
      value: 'hts-report',
      text: 'HTS REPORT',
      permissionKey: 'report_hts_report',
    },
    // {
    //   key: 'hts-register',
    //   value: 'hts-register',
    //   text: 'HTS REGISTER',
    //   permissionKey: 'report_hts_register',
    // },
    // {
    //   key: 'hivst-report',
    //   value: 'hivst-report',
    //   text: 'HIVST REPORT',
    //   permissionKey: 'report_hivst_report',
    // },
    {
      key: 'hts-index-report',
      value: 'hts-index-report',
      text: 'HTS INDEX REPORT',
      permissionKey: 'report_hts_index_report',
    },
  ];

  const reportBiometric = [
    { key: 'biometric', value: 'biometric', text: 'BIOMETRIC DATA' , permissionKey: 'report_biometric_data'},
  ];
  // const monthSummaryReport = [
  //   { key: 'PMTCT-MSF', value: 'PMTCT-MSF', text: 'PMTCT Monthly Summary',
  //     permissionKey: 'report_pmtct_msf'
  //   },
    // { key: 'HTS-MSF', value: 'HTS-MSF', text: 'HTS Monthly Summary', 
    //   permissionKey: 'report_pmtct_msf' }
  // ];

  const reportPrevention = [
    { key: 'prep-report', value: 'prep-report', text: 'PrEP Cross Sectional', permissionKey: 'report_prep_cross_sectional' },
    { key: 'prep-longitudinal-report', value: 'prep-longitudinal-report', text: 'PrEP Longitudinal REPORT', permissionKey: 'report_prep_longitudinal' },
  ];

  const reportPMTCT = [
    { key: 'pmtct-hts', value: 'pmtct-hts', text: 'PMTCT HTS', permissionKey: 'report_pmtct_hts' },
    { key: 'pmtct-maternal-cohort', value: 'pmtct-maternal-cohort', text: 'CHILD Follow-up report', permissionKey: 'report_child_followup_' },
  ];

  const reportPsychosocial = [
    { key: 'mhpss-report', value: 'mhpss-report', text: 'MHPSS Report', permissionKey: 'report_mhpss_report' },
  ];

  const reportMsfs = [
    // { key: 'PMTCT-MSF', value: 'PMTCT-MSF', text: 'PMTCT Monthly Summary', permissionKey: 'report_pmtct_msf' },
    { key: 'hts-msf', value: 'hts-msf', text: 'HTS Monthly Summary', permissionKey: 'report_pmtct_msf' }, //TODO: Consider creating a separate permission for HTS_MSF if needed.
    { key: 'prep-msf', value: 'prep-msf', text: 'PrEP Monthly Summary Form', permissionKey: 'report_prep_monthly_summary_form' },

  ];

  const reportOptions = [
    { key: 'radet', value: 'radet', text: 'RADET', permissionKey: 'report_radet' },
    { key: 'appointment', value: 'appointment', text: 'APPOINTMENT', permissionKey: 'report_appointment' },
    { key: 'line-list', value: 'line-list', text: 'PATIENT LINE LIST', permissionKey: 'report_patient_line_list' },
    { key: 'pharmacy-report', value: 'pharmacy-report', text: 'PHARMACY DATA', permissionKey: 'report_pharmacy_data' },
    { key: 'laboratory-report', value: 'laboratory-report', text: 'LABORATORY DATA', permissionKey: 'report_laboratory_data' },
    { key: 'clinic-data-report', value: 'clinic-data-report', text: 'CLINIC DATA REPORT', permissionKey: 'report_clinic_data_report' },
    { key: 'client-verification', value: 'client-verification', text: 'CLIENT VERIFICATION', permissionKey: 'report_client_verification' },
    { key: 'tb-report-longitudinal', value: 'tb-report-longitudinal', text: 'TB LONGITUDINAL REPORT', permissionKey: 'report_tb_longitudinal' },
    { key: 'ncd-report', value: 'ncd-report', text: 'NCD Report', permissionKey: 'report_ncd_report' },
    { key: 'eac-report', value: 'eac-report', text: 'EAC Report', permissionKey: 'report_eac_report' },
    { key: 'ahd-report', value: 'ahd-report', text: 'AHD REPORT', permissionKey: 'report_ahd_report' },
    { key: 'adr-report', value: 'adr-report', text: 'ADR REPORT', permissionKey: 'report_adr_report' },
    { key: 'custom-report', value: 'custom-report', text: 'CUSTOM REPORT', permissionKey: 'report_custom_report' },
  ];

  const permissions = useMemo(() => {
    // RDE takes precedence: Full access to all reports (except maybe CUSTOM or sensitive ones)
    if (hasRole("RDE") || hasRole("Super Admin")) {
      return {
        canViewHTSReport: true,
        canViewHTSRegister: true,
        canViewHIVSTReport: true,
        canViewHTSIndexReport: true,
        canViewRadet: true,
        canViewAppointment: true,
        canViewPatientLineList: true,
        canViewPharmacyData: true,
        canViewLaboratoryData: true,
        canViewClinicData: true,
        canViewClientVerification: true,
        canViewTbLongitudinal: true,
        canViewNcdReport: true,
        canViewEACReport: true,
        canViewAHDReport: true,
        canViewADRReport: true,
        canViewCustomReport: true,
        canViewPMTCTHTS: true,
        canViewPMTCTMaternalCohort: true,
        canViewPrepCrossSectional: true,
        canViewPrepLongitudinal: true,
        canViewPrepMSF: true,
        canViewHTS_MSF: true,
        canViewPMTCTMSF: true,
        canViewBiometricData: true,
        canViewMHPSSReport: true,
      };
    }

    // For non-RDE users: Permission-based access
    return {
      canViewHTSReport: hasPermission("report_hts_report"),
      canViewHTSRegister: hasPermission("report_hts_register"),
      canViewHIVSTReport: hasPermission("report_hivst_report"),
      canViewHTSIndexReport: hasPermission("report_hts_index_report"),

      canViewRadet: hasPermission("report_radet"),
      canViewAppointment: hasPermission("report_appointment"),
      canViewPatientLineList: hasPermission("report_patient_line_list"),
      canViewPharmacyData: hasPermission("report_pharmacy_data"),
      canViewLaboratoryData: hasPermission("report_laboratory_data"),
      canViewClinicData: hasPermission("report_clinic_data_report"),
      canViewClientVerification: hasPermission("report_client_verification"),
      canViewTbLongitudinal: hasPermission("report_tb_longitudinal"),
      canViewNcdReport: hasPermission("report_ncd_report"),
      canViewEACReport: hasPermission("report_eac_report"),
      canViewAHDReport: hasPermission("report_ahd_report"),
      canViewADRReport: hasPermission("report_adr_report"),
      canViewCustomReport: hasPermission("report_custom_report"),

      canViewPMTCTHTS: hasPermission("report_pmtct_hts"),
      canViewPMTCTMaternalCohort: hasPermission("report_pmtct_maternal_cohort"),

      canViewPrepCrossSectional: hasPermission("report_prep_cross_sectional"),
      canViewPrepLongitudinal: hasPermission("report_prep_longitudinal"),
      canViewPrepMSF: hasPermission("report_prep_monthly_summary_form"),
      canViewPMTCTMSF: hasPermission("report_pmtct_msf"),
      canViewHTS_MSF: hasPermission("report_pmtct_msf"), //TODO: Consider creating a separate permission for HTS_MSF if needed.
      canViewBiometricData: hasPermission("report_biometric_data"),
      canViewMHPSSReport: hasPermission("report_mhpss_report"),
    };
  }, [hasRole, hasPermission, hasAnyPermission]);

  const hasAnyReportAccess = () => {
    const allReports = [
      ...reportSurveillance,
      ...reportOptions,
      ...reportBiometric,
      ...reportPrevention,
      ...reportPMTCT,
      ...reportPsychosocial,
      ...reportMsfs,
      // ...monthSummaryReport,
    ];
    return allReports.some(option =>
        hasRole("RDE")  || hasRole("Super Admin")|| hasPermission(option.permissionKey)
    );
  };

  const hasAnyAccess = (reportList) => {
    return reportList.some(option =>
        hasRole("Super Admin") || hasRole("RDE") || hasPermission(option.permissionKey)
    );
  };

  const hasBasicAccess = () => {
    const basicReports = [
      { value: 'radet', perm: 'report_radet' },
      { value: 'biometric', perm: 'report_biometric_data' },
      { value: 'hts-report', perm: 'report_hts_report' },
      { value: 'prep-report', perm: 'report_prep_cross_sectional' },
    ];

    return basicReports.some(r =>
        hasRole("Super Admin")  || hasRole("RDE") || hasPermission(r.perm)
    );
  };

  const renderComponent = () => {
    switch (activeItem1) {
      case 'radet':
        return (
          <Radet
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'appointment':
        return (
          <Appointment
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'line-list':
        return (
          <PatientLineList
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'pharmacy-report':
        return (
          <PharmacyReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'biometric':
        return (
          <BiometricReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'laboratory-report':
        return (
          <LaboratoryReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'tb-report':
        return (
          <TbReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
        case 'tb-report-longitudinal':
          return (
            <TbReportLongitudinal
              handleItemClick={handleItemClick1}
              setCompleted={setCompleted}
              completed={completed}
            />
          );
      case 'ncd-report':
        return (
          <NcdReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'eac-report':
        return (
          <EACReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'hts-report':
        return (
          <HTSReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'hts-register':
        return (
          <HtsRegister
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'prep-report':
        return (
          <PrepReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'prep-longitudinal-report':
        return (
          <PrepLongitudinalReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'clinic-data-report':
        return (
          <ClinicData
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'client-verification':
        return (
          <ClientVerification
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'index-elicitation':
        return (
          <IndexElicitation
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'pmtct-hts':
        return (
          <PmtctHtsReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'pmtct-maternal-cohort':
        return (
          <PmtctMaternalCohortReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'ahd-report':
        return (
          <AhdReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'mhpss-report':
        return (
          <MhpssReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'kp-prev-report':
        return (
          <KpPrevReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'hivst-report':
        return (
          <HIVST
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'hts-index-report':
        return (
          <HTSIndexReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      // case "kp-prev-report":
      //   return <KpPrevReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case 'hivst-report':
        return (
          <HIVST
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'hts-index-report':
        return (
          <HTSIndexReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );

      case 'adr-report':
        return (
          <ADRReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'custom-report':
        return (
          <CustomReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'PMTCT-MSF':
        return (
          <PMTCTMonthlySummaryReport
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      case 'prep-msf':
        return (
          <PrEPMSF
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
        case 'hts-msf':
        return (
          <HTS_MSF
            handleItemClick={handleItemClick1}
            setCompleted={setCompleted}
            completed={completed}
          />
        );
      default:
        return null;
    }
  };

  return (
    <>
      <ToastContainer autoClose={3000} hideProgressBar />
      <Card>
        <CardBody>
          <div className="row">
            <form>
              <br />
              <br />
              <div className="col-md-3 float-start">
                {!hasAnyReportAccess() ? (
                    <NoAccessCard
                        title="No Report Access"
                        message="You do not have permission to view any reports. Contact your administrator if you believe this is an error."
                    />
                ) : (
                <Menu
                  size="large"
                  vertical
                  style={{ backgroundColor: '#014D88' }}
                >
                  <Menu.Item
                      name="inbox"
                      style={{
                        backgroundColor: '#000',
                      }}
                  >
                    <span style={{color: '#fff'}}>
                      {' '}
                      Search all Report below{' '}
                    </span>
                  </Menu.Item>

                  {hasAnyAccess(reportSurveillance) && (<Accordion
                    expanded={expanded === 'panel2'}
                    onChange={handleChange('panel2')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel2bh-content"
                      id="panel2bh-header"
                    >
                      <Typography sx={{ marginTop: 0, color: '#fff' }}>
                        Surveillance Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                        style={{
                          paddingTop: 0,
                          marginTop: 0,
                          backgroundColor: '#014D88',
                        }}
                    >
                      {Object.values(reportSurveillance).map(option => {
                        const canView =
                            hasRole("Super Admin") ||
                          hasRole("RDE") ||
                            hasPermission(option.permissionKey);
                        // Only render if user has access
                        if (!canView) return null;

                        return (
                            <div
                                key={option.key}
                                style={{
                                  marginTop: '10px',
                                  marginLeft: '10px',
                                  display: 'flex',
                                  justifyContent: 'flex-start',
                                  alignItems: 'center',
                                }}
                            >
                              <div
                                  style={{
                                    width: '10px',
                                    height: '10px',
                                    backgroundColor: 'white',
                                    borderRadius: '50%',
                                  }}
                              />
                              <Typography>
                                <div
                                    style={{
                                      cursor: 'pointer',
                                      marginBottom: '0px',
                                      marginLeft: '10px',
                                      color: activeItem1 === option.value ? 'grey' : '#fff',
                                    }}
                                    onClick={() => handleItemClick1(option.value)}
                                >
                                  {option.text}
                                </div>
                              </Typography>
                              <Divider orientation="horizontal" variant="fullWidth" component="li" />
                            </div>
                        );
                      })}
                    </AccordionDetails>
                  </Accordion>
                  )}
                  { hasAnyAccess(reportOptions) && <Accordion
                    expanded={expanded === 'panel1'}
                    onChange={handleChange('panel1')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel1bh-content"
                      id="panel1bh-header"
                      sx={{ marginTop: 0 }}
                    >
                      <Typography sx={{ marginTop: 0, color: '#fff' }}>
                        Treatment Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportOptions).map(option =>  {
                        const canView =
                          hasRole("Super Admin") ||
                          hasRole("RDE") ||
                        hasPermission(option.permissionKey);
                        if (!canView) return null;

                       return ( <div
                          style={{
                            marginTop: '10px',
                            marginLeft: '10px',
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                          }}
                        >
                          <div
                            style={{
                              width: '10px',
                              height: '10px',
                              backgroundColor: 'white',
                              borderRadius: '50%',
                            }}
                          />
                          <Typography>
                            <div
                              style={{
                                cursor: 'pointer',
                                marginBottom: '0px',
                                marginLeft: '10px',
                                color:
                                  activeItem1 === option.value
                                    ? 'grey'
                                    : '#fff',
                              }}
                              onClick={() => handleItemClick1(option.value)}
                              key={option.key}
                            >
                              {option.text}
                            </div>
                          </Typography>
                          <Divider
                            orientation={'horizontal'}
                            variant="fullWidth"
                            component="li"
                          />
                        </div>
                       );
              })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  { hasAnyAccess(reportBiometric) && <Accordion
                    expanded={expanded === 'panel3'}
                    onChange={handleChange('panel3')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel3bh-content"
                      id="panel3bh-header"
                    >
                      <Typography sx={{ flexShrink: 0, color: '#fff' }}>
                        Biometric Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportBiometric).map(option => {
                        const canView =
                            hasRole("RDE") ||
                            hasRole("Super Admin")
                            hasPermission(option.permissionKey);
                        // Only render if user has access
                        if (!canView) return null;
                        return (
                            <div
                                style={{
                                  marginTop: '10px',
                                  marginLeft: '10px',
                                  display: 'flex',
                                  justifyContent: 'flex-start',
                                  alignItems: 'center',
                                }}
                            >
                              <div
                                  style={{
                                    width: '10px',
                                    height: '10px',
                                    backgroundColor: 'white',
                                    borderRadius: '50%',
                                  }}
                              />
                              <Typography>
                                <div
                                    style={{
                                      cursor: 'pointer',
                                      marginBottom: '0px',
                                      marginLeft: '10px',
                                      color:
                                          activeItem1 === option.value
                                              ? 'grey'
                                              : '#fff',
                                    }}
                                    onClick={() => handleItemClick1(option.value)}
                                    key={option.key}
                                >
                                  {option.text}
                                </div>
                              </Typography>
                              <Divider
                                  orientation={'horizontal'}
                                  variant="fullWidth"
                                  component="li"
                              />
                            </div>
                        );
                      })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  { hasAnyAccess(reportPrevention) && <Accordion
                    expanded={expanded === 'panel4'}
                    onChange={handleChange('panel4')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel3bh-content"
                      id="panel3bh-header"
                    >
                      <Typography sx={{ flexShrink: 0, color: '#fff' }}>
                        Prevention Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportPrevention).map(option => {
                        const canView =
                            hasRole("Super Admin") ||
                            hasRole("RDE") ||
                            hasPermission(option.permissionKey);
                        // Only render if user has access
                        if (!canView) return null;
                        return (
                        <div
                          style={{
                            marginTop: '10px',
                            marginLeft: '10px',
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                          }}
                        >
                          <div
                            style={{
                              width: '10px',
                              height: '10px',
                              backgroundColor: 'white',
                              borderRadius: '50%',
                            }}
                          />
                          <Typography>
                            <div
                              style={{
                                cursor: 'pointer',
                                marginBottom: '0px',
                                marginLeft: '10px',
                                color:
                                  activeItem1 === option.value
                                    ? 'grey'
                                    : '#fff',
                              }}
                              onClick={() => handleItemClick1(option.value)}
                              key={option.key}
                            >
                              {option.text}
                            </div>
                          </Typography>
                          <Divider
                            orientation={'horizontal'}
                            variant="fullWidth"
                            component="li"
                          />
                        </div>
                      );
                      })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  { hasAnyAccess(reportPMTCT) && <Accordion
                    expanded={expanded === 'panel5'}
                    onChange={handleChange('panel5')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel3bh-content"
                      id="panel3bh-header"
                    >
                      <Typography sx={{ flexShrink: 0, color: '#fff' }}>
                        PMTCT Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportPMTCT).map(option =>  {
                        const canView =
                            hasRole("RDE") ||
                            hasRole("Super Admin") ||
                            hasPermission(option.permissionKey);
                        // Only render if user has access
                        if (!canView) return null;
                        return (
                        <div
                          style={{
                            marginTop: '10px',
                            marginLeft: '10px',
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                          }}
                        >
                          <div
                            style={{
                              width: '10px',
                              height: '10px',
                              backgroundColor: 'white',
                              borderRadius: '50%',
                            }}
                          />
                          <Typography>
                            <div
                              style={{
                                cursor: 'pointer',
                                marginBottom: '0px',
                                marginLeft: '10px',
                                color:
                                  activeItem1 === option.value
                                    ? 'grey'
                                    : '#fff',
                              }}
                              onClick={() => handleItemClick1(option.value)}
                              key={option.key}
                            >
                              {option.text}
                            </div>
                          </Typography>
                          <Divider
                            orientation={'horizontal'}
                            variant="fullWidth"
                            component="li"
                          />
                        </div>
                      );
                      })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  {hasAnyAccess(reportPsychosocial) && <Accordion
                    expanded={expanded === 'panel6'}
                    onChange={handleChange('panel6')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel3bh-content"
                      id="panel3bh-header"
                    >
                      <Typography sx={{ flexShrink: 0, color: '#fff' }}>
                        Psychosocial Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportPsychosocial).map(option => {
                        const canView =
                            hasRole("RDE") ||
                            hasRole("Super Admin") ||
                            hasPermission(option.permissionKey);
                        // Only render if user has access
                        if (!canView) return null;
                        return (
                        <div
                          style={{
                            marginTop: '10px',
                            marginLeft: '10px',
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                          }}
                        >
                          <div
                            style={{
                              width: '10px',
                              height: '10px',
                              backgroundColor: 'white',
                              borderRadius: '50%',
                            }}
                          />
                          <Typography>
                            <div
                              style={{
                                cursor: 'pointer',
                                marginBottom: '0px',
                                marginLeft: '10px',
                                color:
                                  activeItem1 === option.value
                                    ? 'grey'
                                    : '#fff',
                              }}
                              onClick={() => handleItemClick1(option.value)}
                              key={option.key}
                            >
                              {option.text}
                            </div>
                          </Typography>
                          <Divider
                            orientation={'horizontal'}
                            variant="fullWidth"
                            component="li"
                          />
                        </div>
                        );
                      })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  { hasAnyAccess(reportMsfs) && <Accordion
                    expanded={expanded === 'panel7'}
                    onChange={handleChange('panel7')}
                    style={{ backgroundColor: '#014D88' }}
                  >
                    <AccordionSummary
                      expandIcon={<ExpandMoreIcon />}
                      aria-controls="panel3bh-content"
                      id="panel3bh-header"
                    >
                      <Typography sx={{ flexShrink: 0, color: '#fff' }}>
                        Monthly Summary Form Report
                      </Typography>
                    </AccordionSummary>
                    <AccordionDetails
                      style={{
                        paddingTop: 0,
                        marginTop: 0,
                        backgroundColor: '#014D88',
                      }}
                    >
                      {Object.values(reportMsfs).map(option => {
                        const canView =
                            hasRole("RDE") ||
                            hasRole("Super Admin") ||
                            hasPermission(option.permissionKey);
                        if (!canView) return null;
                        return (
                        <div
                          style={{
                            marginTop: '10px',
                            marginLeft: '10px',
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                          }}
                        >
                          <div
                            style={{
                              width: '10px',
                              height: '10px',
                              backgroundColor: 'white',
                              borderRadius: '50%',
                            }}
                          />
                          <Typography>
                            <div
                              style={{
                                cursor: 'pointer',
                                marginBottom: '0px',
                                marginLeft: '10px',
                                color:
                                  activeItem1 === option.value
                                    ? 'grey'
                                    : '#fff',
                              }}
                              onClick={() => handleItemClick1(option.value)}
                              key={option.key}
                            >
                              {option.text}
                            </div>
                          </Typography>
                          <Divider
                            orientation={'horizontal'}
                            variant="fullWidth"
                            component="li"
                          />
                        </div>
                      )
                      })}
                    </AccordionDetails>
                  </Accordion>
                  }
                  {/* monthly Summary Form Accordion */}
                  {/* <Accordion expanded={expanded === 'panel7'} onChange={handleChange('panel7')} style={{ backgroundColor: "#014D88" }}>
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel3bh-content"
          id="panel3bh-header"
        >

          <Typography sx={{  flexShrink: 0, color: "#fff" }}>
          Select Monthly Summary Report
          </Typography>
        </AccordionSummary>
        <AccordionDetails style={{paddingTop: 0, marginTop:0, backgroundColor: "#014D88" }}>
          {Object.values(monthSummaryReport).map((option) => (<div style={{marginTop:"10px", marginLeft: "10px", display:"flex", justifyContent:"flex-start", alignItems:"center"}}>
            <div style={{width:"10px", height:"10px", backgroundColor:"white", borderRadius:"50%"}}/>
    <Typography><div style={{cursor:"pointer", marginBottom: "0px",  marginLeft: "10px", color: "#fff"}} onClick={() => handleItemClick1(option.value)} key={option.key}>{option.text}</div></Typography>
    <Divider orientation={"horizontal"} variant="fullWidth" component="li"/></div>
  ))}
        </AccordionDetails>
      </Accordion> */}
                  {/*End of monthly Summary Form Accordion  Accordion  */}

                  <br />

                  { hasBasicAccess() &&  <Menu.Item
                    name="inbox"
                    style={{
                      backgroundColor: '#000',
                    }}
                  >
                    <span style={{ color: '#fff' }}> Basic Report below </span>
                  </Menu.Item>
                  }
                  {(hasRole("RDE") ||  hasRole("Super Admin") || hasPermission("report_radet")) && <Menu.Item
                    name="inbox"
                    active={activeItem === 'radet'}
                    onClick={() => handleItemClick1('radet')}
                    style={{
                      backgroundColor: activeItem === 'radet' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}> RADET </span>
                  </Menu.Item>
                  }
                  {(hasRole("RDE") ||  hasRole("Super Admin") || hasPermission("report_biometric_data")) && <Menu.Item
                    name="inbox"
                    active={activeItem === 'biometric'}
                    onClick={() => handleItemClick1('biometric')}
                    style={{
                      backgroundColor: activeItem === 'biometric' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}>BIOMETRIC DATA</span>
                  </Menu.Item>
                  }
                  { (hasRole("RDE") ||  hasRole("Super Admin") || hasPermission("report_hts_report")) && <Menu.Item
                    name="inbox"
                    active={activeItem === 'hts-report'}
                    onClick={() => handleItemClick1('hts-report')}
                    style={{
                      backgroundColor:
                        activeItem === 'hts-report' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}>HTS REPORT</span>
                  </Menu.Item>
                  }
                  {( hasRole("RDE") ||  hasRole("Super Admin") || hasPermission("report_prep_cross_sectional")) && <Menu.Item
                    name="inbox"
                    active={activeItem === 'prep-report'}
                    onClick={() => handleItemClick1('prep-report')}
                    style={{
                      backgroundColor:
                        activeItem === 'prep-report' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}>PrEP Cross Sectional Report</span>
                  </Menu.Item>
                    }
                </Menu>
                )}
              </div>

              <div
                className="col-md-9 float-end"
                style={{ backgroundColor: '#fff' }}
              >
                {renderComponent()}
                {activeItem === 'line-list' && (
                  <PatientLineList
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'appointment' && (
                  <Appointment
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'radet' && (
                  <Radet
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === 'biometric' && (
                  <BiometricReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'pharmacy-report' && (
                  <PharmacyReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'laboratory-report' && (
                  <LaboratoryReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === 'tb-report' && (
                  <TbReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'ncd-report' && (
                  <NcdReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === 'eac-report' && (
                  <EACReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === 'hts-report' && (
                  <HTSReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'hts-register' && (
                  <HtsRegister
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'prep-report' && (
                  <PrepReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'prep-longitudinal-report' && (
                  <PrepLongitudinalReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'clinic-data-report' && (
                  <ClinicData
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === 'client-verification' && (
                  <ClientVerification
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'index-elicitation' && (
                  <IndexElicitation
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'pmtct-hts' && (
                  <PmtctHtsReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'pmtct-maternal-cohort' && (
                  <PmtctMaternalCohortReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'ahd-report' && (
                  <AhdReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'mhpss-report' && (
                  <MhpssReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {/* {activeItem === "kp-prev-report" && (
                  <KpPrevReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )} */}
                {activeItem === 'hivst-report' && (
                  <HIVST
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'adr-report' && (
                  <ADRReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === 'custom-report' && (
                  <CustomReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
              </div>
            </form>
          </div>
        </CardBody>
      </Card>
    </>
  );
};

export default Reports;
