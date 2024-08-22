import React, { useEffect, useState } from "react";
import axios from "axios";
import { FormGroup, Label, CardBody } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { Card } from "@material-ui/core";
// import {Link, useHistory, useLocation} from "react-router-dom";
// import {TiArrowBack} from 'react-icons/ti'
import { token, url as baseUrl } from "../../../api";
import "react-phone-input-2/lib/style.css";
import { Button } from "semantic-ui-react";
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import { Message } from "semantic-ui-react";
import ProgressComponent from "./ProgressComponent";

const useStyles = makeStyles((theme) => ({
  card: {
    margin: theme.spacing(20),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  cardBottom: {
    marginBottom: 20,
  },
  Select: {
    height: 45,
    width: 300,
  },
  button: {
    margin: theme.spacing(1),
  },
  root: {
    flexGrow: 1,
    maxWidth: 752,
  },
  demo: {
    backgroundColor: theme.palette.background.default,
  },
  inline: {
    display: "inline",
  },
  error: {
    color: "#f85032",
    fontSize: "12.8px",
  },
}));

const ClientVerification = (props) => {
  let currentDate = new Date().toISOString().split("T")[0];
  const classes = useStyles();
  const [loading, setLoading] = useState(false);
  const [facilities, setFacilities] = useState([]);
  const [objValues, setObjValues] = useState({
    organisationUnitId: "",
    organisationUnitName: "",
  });
  useEffect(() => {
    Facilities();
  }, []);
  //Get list of WhoStaging
  const Facilities = () => {
    axios
      .get(`${baseUrl}account`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        console.log(response.data);
        setFacilities(response.data.applicationUserOrganisationUnits);
      })
      .catch((error) => {
        //console.log(error);
      });
  };
  // const handleInputChange = (e) => {
  //   setObjValues({
  //     ...objValues,
  //     [e.target.name]: e.target.value,
  //       organisationUnitName: e.target.innerText,
  //   });
  // };
  const handleInputChange = (e) => {
    const selectedOption = e.target.options[e.target.selectedIndex];
    const selectedValue = e.target.value;
    objValues.organisationUnitName = selectedOption.innerText;
    setObjValues(prevValues => ({
      ...prevValues,
      [e.target.name]: selectedValue,
    }));
};

  const handleSubmit = (e) => {
    e.preventDefault();
      setLoading(true);
      
    axios
      .get(`${baseUrl}reporting/client-service-list/${objValues.organisationUnitId}`, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: "blob",
      })
      .then((response) => {
        setLoading(false);
        // const fileName = `${objValues.organisationUnitName} Client Verification${currentDate}`;
        const fileName = `${objValues.organisationUnitName} Client Verification${currentDate}`;
        const responseData = response.data;
        let blob = new Blob([responseData], {
          type: "application/octet-stream",
        });
        FileSaver.saveAs(blob, `${fileName}.xlsx`);
        //toast.success(" Save successful");
        //props.setActiveContent('recent-history')
      })
      .catch((error) => {
        setLoading(false);
        if (error.response && error.response.data) {
          let errorMessage =
            error.response.data.apierror &&
            error.response.data.apierror.message !== ""
              ? error.response.data.apierror.message
              : "Something went wrong, please try again";
          toast.error(errorMessage);
        } else {
          toast.error("Something went wrong. Please try again...");
        }
      });
  };

  return (
    <>
      <Card>
        <CardBody>
          <h2 style={{ color: "#000" }}>CLIENT VERIFICATION REPORT</h2>
          <br />
          <>
            <div className="row">
              <div className="form-group  col-md-6">
                <FormGroup>
                  <Label>Facility*</Label>
                  <select
                    className="form-control"
                    name="organisationUnitId"
                    id="organisationUnitId"
                    value={objValues.organisationUnitId}
                    onChange={handleInputChange}
                    style={{
                      border: "1px solid #014D88",
                      borderRadius: "0.2rem",
                    }}
                  >
                    <option value={""}></option>
                    {facilities.map((value) => (
                      <option key={value.id} value={value.organisationUnitId}>
                        {value.organisationUnitName}
                      </option>
                    ))}
                  </select>
                </FormGroup>
              </div>

              <br />
              <div className="row">
                <div className="form-group mb-3 col-md-6">
                  <Button
                    type="submit"
                    content="Generate Report"
                    icon="right arrow"
                    labelPosition="right"
                    style={{ backgroundColor: "#014d88", color: "#fff" }}
                    onClick={handleSubmit}
                    // disabled={
                    //   objValues.organisationUnitId === "" ? true : false
                    // }
                    disabled={objValues.organisationUnitId === "" || loading} 
                  />
                </div>
              </div>

              {loading && (
                <Message icon>
                                  <Message.Content>
                                        <ProgressComponent/>
                                  </Message.Content>
                                </Message>
              )}
            </div>
          </>
        </CardBody>
      </Card>
    </>
  );
};

export default ClientVerification;
