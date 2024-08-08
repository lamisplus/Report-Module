import React, { useState } from "react";
import { Card, CardBody } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import "react-phone-input-2/lib/style.css";
import { Icon, Menu, Dropdown } from "semantic-ui-react";
import "semantic-ui-css/semantic.min.css";
import PatientLineList from "./PatientLineList";
import Appointment from "./Appointment";
import Radet from "./Radet";
import BiometricReport from "./BiometricReport";
import PharmacyReport from "./PharmacyReport";
import LaboratoryReport from "./LaboratoryReport";
import HTSReport from "./HTSReport";
import HtsRegister from "./HtsRegister";
import PrepReport from "./PrepReport";
import ClinicData from "./ClinicData";
import ClientVerification from "./ClientVerification";
import TbReport from "./TbReport";
import IndexElicitation from "./IndexElicitation";
import PmtctHtsReport from "./PmtctHtsReport";
import PmtctMaternalCohortReport from "./PmtctMaternalCohortReport";
import NcdReport from "./NcdReport";
import EACReport from "./EACReport";
import AhdReport from "./AhdReport";
import PrepLongitudinalReport from "./PrepLongitudinalReport";
import MhpssReport from "./MhpssReport";
import KpPrevReport from "./KpPrevReport";
import HIVST from "./HIVSTReport";
import HTSIndexReport from "./HTSIndexReport";
import CustomReport from "./CustomReport";
import ADRReport from "./ADRReport"

const useStyles = makeStyles((theme) => ({
  error: {
    color: "#f85032",
    fontSize: "12.8px",
  },
  success: {
    color: "#4BB543 ",
    fontSize: "11px",
  },
}));

const Reports = (props) => {
  const classes = useStyles();
  const [saving, setSaving] = useState(false);
  const [activeItem, setactiveItem] = useState("basic");
  const [activeItem1, setActiveItem1] = useState("basic");

  const [completed, setCompleted] = useState([]);
  const handleItemClick = (activeItem) => {
    setactiveItem(activeItem);
    //setCompleted({...completed, ...completedMenu})
  };


  const handleItemClick1 = (value) => {
    setActiveItem1(value);
    console.log(value); // This should print the selected option
  };

  const reportSurveillance = [
    { key: 'hts-report', value: 'hts-report', text: 'HTS REPORT' },
    { key: 'hts-register', value: 'hts-register', text: 'HTS REGISTER' },
    { key: 'hivst-report', value: 'hivst-report', text: 'HIVST REPORT' },
    // { key: 'hts-index-report', value: 'hts-index-report', text: 'HTS INDEX REPORT' },
  ]

  const reportBiometric = [
      { key: 'biometric', value: 'biometric', text: 'BIOMETRIC DATA' },
    
  ]

  const reportPrevention = [
    { key: 'prep-report', value: 'prep-report', text: 'Prep REPORT' },
    { key: 'prep-longitudinal-report', value: 'prep-longitudinal-report', text: 'PrEP LONGITUDINAL REPORT' },
    { key: 'kp-prev-report', value: 'kp-prev-report', text: 'KP PREV REPORT' },
  ]

  const reportPMTCT = [
    { key: 'pmtct-hts', value: 'pmtct-hts', text: 'PMTCT HTS' },
    { key: 'pmtct-maternal-cohort', value: 'pmtct-maternal-cohort', text: 'PMTCT MATERNAL COHORT' },

  ]

  const reportPsychosocial = [
    { key: 'mhpss-report', value: 'mhpss-report', text: 'MHPSS Report' },

  ]

  const reportOptions = [
    { key: 'radet', value: 'radet', text: 'RADET' },
    { key: 'appointment', value: 'appointment', text: 'APPOINTMENT' },
    { key: 'line-list', value: 'line-list', text: 'PATIENT LINE LIST' },
    { key: 'pharmacy-report', value: 'pharmacy-report', text: 'PHARMACY DATA' },
    { key: 'laboratory-report', value: 'laboratory-report', text: 'LABORATORY DATA' },
    { key: 'clinic-data-report', value: 'clinic-data-report', text: 'CLINIC DATA REPORT' },
    { key: 'client-verification', value: 'client-verification', text: 'CLIENT VERIFICATION' },
    { key: 'tb-report', value: 'tb-report', text: 'TB REPORT' },
    { key: 'ncd-report', value: 'ncd-report', text: 'NCD Report' },
    { key: 'eac-report', value: 'eac-report', text: 'EAC Report' },
    { key: 'index-elicitation', value: 'index-elicitation', text: 'INDEX ELICITATION' },
    { key: 'ahd-report', value: 'ahd-report', text: 'AHD REPORT' },
    { key: 'adr-report', value: 'adr-report', text: 'ADR REPORT' },
    { key: 'custom-report', value: 'custom-report', text: 'CUSTOM REPORT' },
  ]


  const renderComponent = () => {

    switch (activeItem1) {
      case 'radet':
        console.log("Got here   :" + activeItem1);
        return <Radet handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "appointment":
        return <Appointment handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "line-list":
        return <PatientLineList handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "pharmacy-report":
        return <PharmacyReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;


      case "biometric":
        return <BiometricReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "laboratory-report":
        return <LaboratoryReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "tb-report":
        return <TbReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "ncd-report":
        return <NcdReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "eac-report":
        return <EACReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "hts-report":
        return <HTSReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "hts-register":
        return <HtsRegister handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "prep-report":
        return <PrepReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "prep-longitudinal-report":
        return <PrepLongitudinalReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "clinic-data-report":
        return <ClinicData handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "client-verification":
        return <ClientVerification handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "index-elicitation":
        return <IndexElicitation handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "pmtct-hts":
        return <PmtctHtsReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "pmtct-maternal-cohort":
        return <PmtctMaternalCohortReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "ahd-report":
        return <AhdReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "mhpss-report":
        return <MhpssReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "kp-prev-report":
        return <KpPrevReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "hivst-report":
        return <HIVST handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "hts-index-report":
        return <HTSIndexReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

      case "adr-report":
        return <ADRReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;
      case "custom-report":
        return <CustomReport handleItemClick={handleItemClick1} setCompleted={setCompleted} completed={completed} />;

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
                  size="medium"
                  vertical
                  style={{ backgroundColor: "#014D88" }}
                >
                  <Menu.Item
                    name="inbox"
                    style={{
                      backgroundColor: "#000",
                    }}
                  >
                    <span style={{ color: "#fff" }}> Search all Report below </span>
                  </Menu.Item>
                  <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportOptions}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select Treatment Report'
                  />
                  <br/>
                    <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportSurveillance}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select Surveillance Report'
                  />
                  <br/>

                    <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportBiometric}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select Biometric Report'
                  />
                  <br/>

                    <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportPrevention}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select Prevention Report'
                  />
                  <br/>
                  <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportPMTCT}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select PMTCT Report'
                  />
                  <br/>
                  <Dropdown
                    clearable
                    fluid
                    search
                    selection
                    options={reportPsychosocial}
                    onChange={(event, data) => handleItemClick1(data.value)}
                    onClick={(value) => handleItemClick1(value)}
                    placeholder='Select Psychosocial Report'
                  />
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "radet"}
                    onClick={() => handleItemClick1("radet")}
                    style={{
                      backgroundColor: activeItem === "radet" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}> RADET </span>
                  </Menu.Item>

                  {/* <Menu.Item
                    name="spam"
                    active={activeItem === "appointment"}
                    onClick={() => handleItemClick("appointment")}
                    style={{
                      backgroundColor:
                        activeItem === "appointment" ? "#000" : "",
                    }}
                  > */}
                    {/* <Label>4</Label> */}
                    {/* <span style={{ color: "#fff" }}>APPOINTMENT </span>
                  </Menu.Item> */}
                  {/* <Menu.Item
                    name="spam"
                    active={activeItem === "line-list"}
                    onClick={() => handleItemClick("line-list")}
                    style={{
                      backgroundColor: activeItem === "line-list" ? "#000" : "",
                    }}
                  > */}
                    {/* <Label>4</Label> */}
                    {/* <span style={{ color: "#fff" }}>PATIENT LINE LIST</span>
                  </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "pharmacy-report"}
                    onClick={() => handleItemClick("pharmacy-report")}
                    style={{
                      backgroundColor:
                        activeItem === "pharmacy-report" ? "#000" : "",
                    }}
                  > */}
                    {/* <span style={{ color: "#fff" }}>PHARMACY DATA</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "biometric"}
                    onClick={() => handleItemClick1("biometric")}
                    style={{
                      backgroundColor: activeItem === "biometric" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>BIOMETRIC DATA</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "laboratory-report"}
                    onClick={() => handleItemClick("laboratory-report")}
                    style={{
                      backgroundColor:
                        activeItem === "laboratory-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>LABORATORY DATA</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "hts-report"}
                    onClick={() => handleItemClick1("hts-report")}
                    style={{
                      backgroundColor:
                        activeItem === "hts-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>HTS REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "hts-register"}
                    onClick={() => handleItemClick("hts-register")}
                    style={{
                      backgroundColor:
                        activeItem === "hts-register" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>HTS REGISTER</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "prep-report"}
                    onClick={() => handleItemClick1("prep-report")}
                    style={{
                      backgroundColor:
                        activeItem === "prep-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>Prep REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "prep-longitudinal-report"}
                    onClick={() => handleItemClick("prep-longitudinal-report")}
                    style={{
                      backgroundColor:
                        activeItem === "prep-longitudinal-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PrEP LONGITUDINAL REPORT</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "clinic-data-report"}
                    onClick={() => handleItemClick("clinic-data-report")}
                    style={{
                      backgroundColor:
                        activeItem === "clinic-data-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>CLINIC DATA REPORT</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "client-verification"}
                    onClick={() => handleItemClick("client-verification")}
                    style={{
                      backgroundColor:
                        activeItem === "client-verification" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>CLIENT VERIFICATION</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "tb-report"}
                    onClick={() => handleItemClick("tb-report")}
                    style={{
                      backgroundColor: activeItem === "tb-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>TB REPORT</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "ncd-report"}
                    onClick={() => handleItemClick("ncd-report")}
                    style={{
                      backgroundColor:
                        activeItem === "ncd-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>NCD Report</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "eac-report"}
                    onClick={() => handleItemClick("eac-report")}
                    style={{
                      backgroundColor:
                        activeItem === "eac-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>EAC Report</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}


                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "index-elicitation"}
                    onClick={() => handleItemClick("index-elicitation")}
                    style={{
                      backgroundColor:
                        activeItem === "index-elicitation" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>INDEX ELICITATION</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "pmtct-hts"}
                    onClick={() => handleItemClick("pmtct-hts")}
                    style={{
                      backgroundColor: activeItem === "pmtct-hts" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PMTCT HTS</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "pmtct-maternal-cohort"}
                    onClick={() => handleItemClick("pmtct-maternal-cohort")}
                    style={{
                      backgroundColor:
                        activeItem === "pmtct-maternal-cohort" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PMTCT MATERNAL COHORT</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "ahd-report"}
                    onClick={() => handleItemClick("ahd-report")}
                    style={{
                      backgroundColor: activeItem === "ahd-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>AHD REPORT</span> */}

                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "mhpss-report"}
                    onClick={() => handleItemClick("mhpss-report")}
                    style={{
                      backgroundColor:
                        activeItem === "mhpss-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>MHPSS Report</span> */}
                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "kp-prev-report"}
                    onClick={() => handleItemClick("kp-prev-report")}
                    style={{
                      backgroundColor: activeItem === "kp-prev-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>KP PREV REPORT</span> */}
                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "hivst-report"}
                    onClick={() => handleItemClick("hivst-report")}
                    style={{
                      backgroundColor: activeItem === "hivst-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>HIVST REPORT</span> */}
                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                      name="inbox"
                       active={activeItem === "hts-index-report"}
                       onClick={() => handleItemClick("hts-index-report")}
                        style={{
                        backgroundColor: activeItem === "hts-index-report" ? "#000" : "",
                         }}
                     >
                     <span style={{ color: "#fff" }}>HTS INDEX REPORT</span>*/}
                  {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}
                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "adr-report"}
                    onClick={() => handleItemClick("adr-report")}
                    style={{
                      backgroundColor: activeItem === "adr-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>ADR REPORT</span> */}
                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}

                  {/* <Menu.Item
                    name="inbox"
                    active={activeItem === "custom-report"}
                    onClick={() => handleItemClick("custom-report")}
                    style={{
                      backgroundColor: activeItem === "custom-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>CUSTOM REPORT</span> */}
                    {/* <Label color='teal'>5</Label> */}
                  {/* </Menu.Item> */}


                </Menu>
              </div>

              <div
                className="col-md-9 float-end"
                style={{ backgroundColor: "#fff" }}
              >
                {renderComponent()}
                {activeItem === "line-list" && (
                  <PatientLineList
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "appointment" && (
                  <Appointment
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "radet" && (
                  <Radet
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === "biometric" && (
                  <BiometricReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "pharmacy-report" && (
                  <PharmacyReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "laboratory-report" && (
                  <LaboratoryReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === "tb-report" && (
                  <TbReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "ncd-report" && (
                  <NcdReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === "eac-report" && (
                  <EACReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === "hts-report" && (
                  <HTSReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "hts-register" && (
                  <HtsRegister
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "prep-report" && (
                  <PrepReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "prep-longitudinal-report" && (
                  <PrepLongitudinalReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "clinic-data-report" && (
                  <ClinicData
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}

                {activeItem === "client-verification" && (
                  <ClientVerification
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "index-elicitation" && (
                  <IndexElicitation
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "pmtct-hts" && (
                  <PmtctHtsReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "pmtct-maternal-cohort" && (
                  <PmtctMaternalCohortReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "ahd-report" && (
                  <AhdReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "mhpss-report" && (
                  <MhpssReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )
                }
                {activeItem === "kp-prev-report" && (
                  <KpPrevReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "hivst-report" && (
                  <HIVST
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {/* {activeItem === "hts-index-report" && (
                  <HTSIndexReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )} */}
                {activeItem === "adr-report" && (
                  <ADRReport
                    handleItemClick={handleItemClick}
                    setCompleted={setCompleted}
                    completed={completed}
                  />
                )}
                {activeItem === "custom-report" && (
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
