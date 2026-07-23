import React, { useEffect, useState } from "react";
import axios from "axios";
import { FormGroup, Label, CardBody, Input } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { Card } from "@material-ui/core";
import { token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button, Dropdown } from 'semantic-ui-react'
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import { Message } from 'semantic-ui-react'
import ProgressComponent from "./ProgressComponent"


const useStyles = makeStyles((theme) => ({
    card: {
        margin: theme.spacing(20),
        display: "flex",
        flexDirection: "column",
        alignItems: "center"
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
        color: '#f85032',
        fontSize: '12.8px'
    }
}));

const HTS_MSF = (props) => {
    let currentDate = new Date().toISOString().split('T')[0]
    const classes = useStyles();
    const [loading, setLoading] = useState(false)
    const [facilities, setFacilities] = useState([]);
    // const [status, setStatus] = useState(true);
    const [objValues, setObjValues] = useState({
        organisationUnitId: "",
        organisationUnitName: "",
        month: "",
    });
    const [months, setMonths] = useState([]);

    useEffect(() => {
        Facilities();
        loadMonths();
    }, []);

    const monthOptions = months.map(month => ({
    key: month,
    text: month,
    value: month
}));

    const Facilities = () => {
        axios
            .get(`${baseUrl}account`,
                { headers: { "Authorization": `Bearer ${token}` } }
            )
            .then((response) => {
                console.log(response.data);
                setFacilities(response.data.applicationUserOrganisationUnits);
            })
            .catch((error) => {
            });

    }

    const loadMonths = () => {
        axios.get(
            `${baseUrl}months`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        )
            .then(response => {
                setMonths(response.data);
            })
            .catch(() => {
                toast.error("Unable to load months");
            });
    };


    const handleInputChange = (e) => {
        const selectedOption = e.target.options ? e.target.options[e.target.selectedIndex] : null;
        const selectedValue = e.target.value;
        const name = e.target.name;

        setObjValues(prevValues => ({
            ...prevValues,
            [name]: selectedValue,
            organisationUnitName: name === "organisationUnitId" && selectedOption ? selectedOption.innerText : prevValues.organisationUnitName,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true)
        console.log(token);

        axios.get(`${baseUrl}hts_msf?facilityId=${objValues.organisationUnitId}&month=${objValues.month}`,
            { headers: { "Authorization": `Bearer ${token}` }, responseType: 'blob' },
        )
            .then(response => {
                setLoading(false)
                const fileName = `${objValues.organisationUnitName} HTS MSF ${currentDate}`
                const responseData = response.data
                let blob = new Blob([responseData], { type: "application/octet-stream" });

                FileSaver.saveAs(blob, `${fileName}.xlsx`);

                toast.success("HTS MSF Report generated successfully");
            })
            .catch(error => {
                setLoading(false)
                if (error.response && error.response.data) {
                    let errorMessage = error.response.data.apierror && error.response.data.apierror.message !== "" ? error.response.data.apierror.message : "Something went wrong, please try again";
                    toast.error(errorMessage);
                }
                else {
                    toast.error("Something went wrong. Please try again...");
                }
            });
    }

    return (
        <>

            <Card >
                <CardBody>

                    <h2 style={{ color: '#000' }}>HTS Monthly Summary Form REPORT</h2>
                    <br />
                    < >
                        <div className="row">
                           <div className="form-group col-md-6">
    <FormGroup>
        <Label>Reporting Month *</Label>

        <Dropdown
            fluid
            search
            selection
            placeholder="Select Month"
            options={monthOptions}
            value={objValues.month}
            onChange={(e, { value }) =>
                setObjValues({
                    ...objValues,
                    month: value
                })
            }
        />
    </FormGroup>
</div>
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Facility*</Label>
                                    <select
                                        className="form-control"
                                        name="organisationUnitId"
                                        id="organisationUnitId"
                                        value={objValues.organisationUnitId}
                                        onChange={handleInputChange}
                                        style={{ border: "1px solid #014D88", borderRadius: "0.2rem" }}
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
                                    <Button type="submit" content='Generate Report' icon='right arrow' labelPosition='right' style={{ backgroundColor: "#014d88", color: '#fff' }} onClick={handleSubmit}
                                        disabled={objValues.organisationUnitId === "" || objValues.month === "" || loading}
                                    />
                                </div>
                            </div>

                            {loading && (
                                <Message icon>
                                    <Message.Content>
                                        <ProgressComponent />
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

export default HTS_MSF