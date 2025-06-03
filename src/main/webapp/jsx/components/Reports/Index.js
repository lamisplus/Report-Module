import React, { useState } from 'react';
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
import TbReportLongitudinal from './TbReportLongitudinal';

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

  const [completed, setCompleted] = useState([]);
  const handleItemClick = activeItem => {
    setactiveItem(activeItem);
  };

  const handleChange = panel => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false);
  };

  const handleItemClick1 = value => {
    setActiveItem1(value);
    console.log(value); // This should print the selected option
  };

  const reportMsfs = [
    { key: 'prep-msf', value: 'prep-msf', text: 'PrEP Monthly Summary Form' },
  ];

  const reportSurveillance = [
    { key: 'hts-report', value: 'hts-report', text: 'HTS REPORT' },
    { key: 'hts-register', value: 'hts-register', text: 'HTS REGISTER' },
    { key: 'hivst-report', value: 'hivst-report', text: 'HIVST REPORT' },
    {
      key: 'hts-index-report',
      value: 'hts-index-report',
      text: 'HTS INDEX REPORT',
    },
  ];

  const reportBiometric = [
    { key: 'biometric', value: 'biometric', text: 'BIOMETRIC DATA' },
  ];
  const monthSummaryReport = [
    { key: 'PMTCT-MSF', value: 'PMTCT-MSF', text: 'PMTCT Monthly Summary' },
  ];
  const reportPrevention = [
    { key: 'prep-report', value: 'prep-report', text: 'PrEP Cross Sectional' },
    {
      key: 'prep-longitudinal-report',
      value: 'prep-longitudinal-report',
      text: 'PrEP Longitudinal REPORT',
    },
    // { key: 'kp-prev-report', value: 'kp-prev-report', text: 'KP PREV REPORT' },
  ];

  const reportPMTCT = [
    { key: 'pmtct-hts', value: 'pmtct-hts', text: 'PMTCT HTS' },
    {
      key: 'pmtct-maternal-cohort',
      value: 'pmtct-maternal-cohort',
      text: 'PMTCT MATERNAL COHORT',
    },
  ];

  const reportPsychosocial = [
    { key: 'mhpss-report', value: 'mhpss-report', text: 'MHPSS Report' },
  ];

  const reportOptions = [
    { key: 'radet', value: 'radet', text: 'RADET' },
    { key: 'appointment', value: 'appointment', text: 'APPOINTMENT' },
    { key: 'line-list', value: 'line-list', text: 'PATIENT LINE LIST' },
    { key: 'pharmacy-report', value: 'pharmacy-report', text: 'PHARMACY DATA' },
    {
      key: 'laboratory-report',
      value: 'laboratory-report',
      text: 'LABORATORY DATA',
    },
    {
      key: 'clinic-data-report',
      value: 'clinic-data-report',
      text: 'CLINIC DATA REPORT',
    },
    {
      key: 'client-verification',
      value: 'client-verification',
      text: 'CLIENT VERIFICATION',
    },
    { key: 'tb-report', value: 'tb-report', text: 'TB REPORT' },
    { key: 'tb-report-longitudinal', value: 'tb-report-longitudinal', text: 'TB LONGITUDINAL REPORT' },
    { key: 'ncd-report', value: 'ncd-report', text: 'NCD Report' },
    { key: 'eac-report', value: 'eac-report', text: 'EAC Report' },
    // { key: 'index-elicitation', value: 'index-elicitation', text: 'INDEX ELICITATION' },
    { key: 'ahd-report', value: 'ahd-report', text: 'AHD REPORT' },
    { key: 'adr-report', value: 'adr-report', text: 'ADR REPORT' },
    { key: 'custom-report', value: 'custom-report', text: 'CUSTOM REPORT' },

  ];

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
                    <span style={{ color: '#fff' }}>
                      {' '}
                      Search all Report below{' '}
                    </span>
                  </Menu.Item>

                  <Accordion
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
                      {Object.values(reportSurveillance).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>

                  <Accordion
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
                      {Object.values(reportOptions).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>

                  <Accordion
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
                      {Object.values(reportBiometric).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>

                  <Accordion
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
                      {Object.values(reportPrevention).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>
                  <Accordion
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
                      {Object.values(reportPMTCT).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>
                  <Accordion
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
                      {Object.values(reportPsychosocial).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>

                  <Accordion
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
                      {Object.values(reportMsfs).map(option => (
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
                      ))}
                    </AccordionDetails>
                  </Accordion>

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

                  <Menu.Item
                    name="inbox"
                    style={{
                      backgroundColor: '#000',
                    }}
                  >
                    <span style={{ color: '#fff' }}> Basic Report below </span>
                  </Menu.Item>

                  <Menu.Item
                    name="inbox"
                    active={activeItem === 'radet'}
                    onClick={() => handleItemClick1('radet')}
                    style={{
                      backgroundColor: activeItem === 'radet' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}> RADET </span>
                  </Menu.Item>

                  <Menu.Item
                    name="inbox"
                    active={activeItem === 'biometric'}
                    onClick={() => handleItemClick1('biometric')}
                    style={{
                      backgroundColor: activeItem === 'biometric' ? '#000' : '',
                    }}
                  >
                    <span style={{ color: '#fff' }}>BIOMETRIC DATA</span>
                  </Menu.Item>

                  <Menu.Item
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

                  <Menu.Item
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
                </Menu>
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
