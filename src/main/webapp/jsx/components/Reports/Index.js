import React, { useState } from "react";
import { Card, CardBody } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import "react-phone-input-2/lib/style.css";
import { Icon, Menu } from "semantic-ui-react";
import "semantic-ui-css/semantic.min.css";
import PatientLineList from "./PatientLineList";
import Appointment from "./Appointment";
import Radet from "./Radet";
import BiometricReport from "./BiometricReport";
import PharmacyReport from "./PharmacyReport";
import LaboratoryReport from "./LaboratoryReport";
import HTSReport from "./HTSReport";
import PrepReport from "./PrepReport";
import ClinicData from "./ClinicData";
import ClientVerification from "./ClientVerification";
import TbReport from "./TbReport";
import IndexElicitation from "./IndexElicitation";
import PmtctHtsReport from "./PmtctHtsReport";
import PmtctMaternalCohortReport from "./PmtctMaternalCohortReport";
import NcdReport from "./NcdReport";
import EACReport from "./EACReport";

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
  const [completed, setCompleted] = useState([]);
  const handleItemClick = (activeItem) => {
    setactiveItem(activeItem);
    //setCompleted({...completed, ...completedMenu})
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
                  size="small"
                  vertical
                  style={{ backgroundColor: "#014D88" }}
                >
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "radet"}
                    onClick={() => handleItemClick("radet")}
                    style={{
                      backgroundColor: activeItem === "radet" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}> RADET </span>
                  </Menu.Item>

                  <Menu.Item
                    name="spam"
                    active={activeItem === "appointment"}
                    onClick={() => handleItemClick("appointment")}
                    style={{
                      backgroundColor:
                        activeItem === "appointment" ? "#000" : "",
                    }}
                  >
                    {/* <Label>4</Label> */}
                    <span style={{ color: "#fff" }}>APPOINTMENT </span>
                  </Menu.Item>
                  <Menu.Item
                    name="spam"
                    active={activeItem === "line-list"}
                    onClick={() => handleItemClick("line-list")}
                    style={{
                      backgroundColor: activeItem === "line-list" ? "#000" : "",
                    }}
                  >
                    {/* <Label>4</Label> */}
                    <span style={{ color: "#fff" }}>PATIENT LINE LIST</span>
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "pharmacy-report"}
                    onClick={() => handleItemClick("pharmacy-report")}
                    style={{
                      backgroundColor:
                        activeItem === "pharmacy-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PHARMACY DATA</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "biometric"}
                    onClick={() => handleItemClick("biometric")}
                    style={{
                      backgroundColor: activeItem === "biometric" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>BIOMETRIC DATA</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "laboratory-report"}
                    onClick={() => handleItemClick("laboratory-report")}
                    style={{
                      backgroundColor:
                        activeItem === "laboratory-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>LABORATORY DATA</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "hts-report"}
                    onClick={() => handleItemClick("hts-report")}
                    style={{
                      backgroundColor:
                        activeItem === "hts-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>HTS REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "prep-report"}
                    onClick={() => handleItemClick("prep-report")}
                    style={{
                      backgroundColor:
                        activeItem === "prep-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>Prep REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "clinic-data-report"}
                    onClick={() => handleItemClick("clinic-data-report")}
                    style={{
                      backgroundColor:
                        activeItem === "clinic-data-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>CLINIC DATA REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>

                  <Menu.Item
                    name="inbox"
                    active={activeItem === "client-verification"}
                    onClick={() => handleItemClick("client-verification")}
                    style={{
                      backgroundColor:
                        activeItem === "client-verification" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>CLIENT VERIFICATION</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>

                  <Menu.Item
                    name="inbox"
                    active={activeItem === "tb-report"}
                    onClick={() => handleItemClick("tb-report")}
                    style={{
                      backgroundColor: activeItem === "tb-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>TB REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "ncd-report"}
                    onClick={() => handleItemClick("ncd-report")}
                    style={{
                      backgroundColor:
                        activeItem === "ncd-report" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>NCD REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                      name="inbox"
                      active={activeItem === "eac-report"}
                      onClick={() => handleItemClick("eac-report")}
                      style={{
                        backgroundColor:
                            activeItem === "ncd-report" ? "#000" : "",
                      }}
                  >
                    <span style={{ color: "#fff" }}>EAC REPORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "index-elicitation"}
                    onClick={() => handleItemClick("index-elicitation")}
                    style={{
                      backgroundColor:
                        activeItem === "index-elicitation" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>INDEX ELICITATION</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "pmtct-hts"}
                    onClick={() => handleItemClick("pmtct-hts")}
                    style={{
                      backgroundColor: activeItem === "pmtct-hts" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PMTCT HTS</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                  <Menu.Item
                    name="inbox"
                    active={activeItem === "pmtct-maternal-cohort"}
                    onClick={() => handleItemClick("pmtct-maternal-cohort")}
                    style={{
                      backgroundColor:
                        activeItem === "pmtct-maternal-cohort" ? "#000" : "",
                    }}
                  >
                    <span style={{ color: "#fff" }}>PMTCT MATERNAL COHORT</span>

                    {/* <Label color='teal'>5</Label> */}
                  </Menu.Item>
                </Menu>
              </div>
              <div
                className="col-md-9 float-end"
                style={{ backgroundColor: "#fff" }}
              >
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
                {activeItem === "prep-report" && (
                  <PrepReport
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
              </div>
            </form>
          </div>
        </CardBody>
      </Card>
    </>
  );
};

export default Reports;
