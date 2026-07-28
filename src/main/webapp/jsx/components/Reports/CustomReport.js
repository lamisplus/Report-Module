import React, { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { FormGroup, Label, CardBody, Input } from "reactstrap";
import { token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button } from 'semantic-ui-react'
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import { Message, TextArea, Dropdown } from 'semantic-ui-react'
import ScrollableDiv from "../Shared/Scrollable"
import ProgressComponent from "./ProgressComponent"

import {
    Card,
    Paper,
    Box,
    Typography,
    TextField
} from "@material-ui/core";

import AssessmentIcon from "@material-ui/icons/Assessment";


const CustomReport = (props) => {
    let currentDate = new Date().toISOString().split('T')[0]
    // const classes = useStyles();
    const [loading, setLoading] = useState(false)
    const [listOfParams, setListOfParams] = useState([]);
    const [facilities, setFacilities] = useState([]);
    const [listOfReport, setListOfReport] = useState([]);
    const [selectedReport, setSelectedReport] = useState("")
    const [customQuery, setCustomQuery] = useState("")
    const [customDataFields, setCustomDataFields] = useState({});
    const [objValues, setObjValues] = useState({
        query: "",
        reportName: "",
        organisationUnitId: "",
        organisationUnitName: "",
        currentDate: currentDate
    })
    const [formData, setFormData] = useState(objValues)

    const loadFacilities = useCallback(async () => {
        try {
            const response = await axios.get(
                `${baseUrl}account`,
                { headers: { "Authorization": `Bearer ${token}` } }
            );
            setFacilities(response.data.applicationUserOrganisationUnits);
        } catch (e) {
            console.log(e);
        }
    }, []);

    const onData = (data) => {
        setCustomDataFields(data);
    }

    const containsTemplateStrings = (obj) => {
        return Object.values(obj).some((value) => {
            return typeof value === 'string' && value.includes('{{');
        });
    };

    const handleInputChange = e => {
        setObjValues({ ...objValues, [e.target.name]: e.target.value });
    }


    function extractPatterns(e) {
        var arr = []
        const pattern = /\{\{([^}]+)\}\}/g;
        let match;
        while ((match = pattern.exec(e)) !== null) {
            const obj = {
                [match[1]]: ""
            }
            arr.push(obj)

        }
        setListOfParams(arr)
    }

    const handleAnalyze = (e) => {
        e.preventDefault();
        const objValuesWithTemplateStrings = containsTemplateStrings(objValues?.query);
        const text = extractPatterns(objValues?.query)
    }

    const handleCancel = (e) => {
        e.preventDefault();
    }

    const handleDryRun = (e) => {
        e.preventDefault();
        var customQuery = objValues?.query;
        customQuery = replaceValues(customQuery, customDataFields);
        customQuery = customQuery.trim().concat(" LIMIT 5");
        setCustomQuery(customQuery);
        console.log(customQuery);

        axios.post(
            `${baseUrl}customized-reports/generate-report`,
            {},
            {
                params: {
                    query: customQuery,
                    reportName: objValues.reportName,
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                responseType: 'blob',
            }
        )
            .then((response) => {
                console.log("Here ******" + response.data);
                setLoading(false);
                const fileName = `${objValues.organisationUnitName} ${objValues.reportName} Report ${currentDate}`;
                const responseData = response.data;
                let blob = new Blob([responseData], { type: "application/octet-stream" });
                FileSaver.saveAs(blob, `${fileName}.xlsx`);
                toast.success("Custom Report generated successfully");
            })
            .catch((error) => {
                setLoading(false);
                if (error.response && error.response.data) {
                    let errorMessage = error.response.data.apierror && error.response.data.apierror.message !== "" ? error.response.data.apierror.message : "Something went wrong, please try again";
                    toast.error(errorMessage);
                } else {
                    toast.error("Something went wrong. Please try again...");
                }
            });
    }

    function replaceValues(query, customDataFields) {
        return query.replace(/{{\s*([^}]+)\s*}}/g, (match, key) => {
            const normalizedKey = key.trim().toLowerCase().replace(' ', '_');
            return customDataFields[normalizedKey] !== undefined ? customDataFields[normalizedKey] : match;
        });
    }

    const handleSaveCustomReport = (e) => {
        e.preventDefault()
        axios
            .post(`${baseUrl}customized-reports`, objValues, { headers: { "Authorization": `Bearer ${token}` } })
            .then(response => {
                getCustomReports();
                toast.success("Custom report successfully saved...")

            })
            .catch(error => {
                console.log(error)
            }

            );
    }

    async function getCustomReports() {
        axios
            .get(`${baseUrl}customized-reports`, { headers: { "Authorization": `Bearer ${token}` } })
            .then((response) => {
                setListOfReport(
                    Object.entries(response.data).map(([key, value]) => ({
                        key: value.query,
                        text: value.reportName,
                        value: value.id,
                    }))
                );
            })
            .catch((error) => { });
    }

    useEffect(() => {
        getCustomReports();
        loadFacilities();
    }, []);

    const handleChange = (e, data) => {
        e.preventDefault();
        setSelectedReport(data.value)
        //api call here
        axios
            .get(`${baseUrl}customized-reports/${data.value}`, { headers: { "Authorization": `Bearer ${token}` } })
            .then((response) => {

                setObjValues(response.data)
            })
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        var customQuery = objValues?.query;
        customQuery = replaceValues(customQuery, customDataFields);
        setCustomQuery(customQuery);
        axios.post(
            `${baseUrl}customized-reports/generate-report`,
            {},
            {
                params: {
                    query: customQuery,
                    reportName: objValues.reportName,
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                responseType: 'blob',
            }
        )
            .then((response) => {
                setLoading(false);
                const fileName = `${objValues.organisationUnitName} ${objValues.reportName} Report ${currentDate}`;
                const responseData = response.data;
                let blob = new Blob([responseData], { type: "application/octet-stream" });
                FileSaver.saveAs(blob, `${fileName}.xlsx`);
                toast.success("Custom Report generated successfully");
            })
            .catch((error) => {
                setLoading(false);
                if (error.response && error.response.data) {
                    let errorMessage = error.response.data.apierror && error.response.data.apierror.message !== "" ? error.response.data.apierror.message : "Something went wrong, please try again";
                    toast.error(errorMessage);
                } else {
                    toast.error("Something went wrong. Please try again...");
                }
            });
    };


    return (
        <>

            <Card >
                <CardBody>

                    <Box mb={3}>
                        <Typography
                            variant="h5"
                            style={{
                                color: "#014D88",
                                fontWeight: 600,
                                display: "flex",
                                alignItems: "center"
                            }}
                        >
                            <AssessmentIcon
                                style={{
                                    marginRight: 10,
                                    color: "#014D88"
                                }}
                            />
                            Custom Report Builder
                        </Typography>

                        <Typography
                            variant="body2"
                            color="textSecondary"
                        >
                            Create, analyze, save and generate
                            custom SQL reports using configurable
                            parameters.
                        </Typography>
                    </Box>
                    <br />
                    <FormGroup>
                        <Label style={{ color: '#014d88', fontWeight: 'bolder' }}>List of Reports <span style={{ cursor: "pointer", color: "blue" }}
                        >
                        </span></Label>
                        <Dropdown
                            placeholder='Select Report'
                            fluid
                            search
                            selection
                            name="reports"
                            id="reports"
                            value={selectedReport}
                            onChange={handleChange}
                            options={listOfReport}
                        />
                    </FormGroup>
                    < >
                        <div className="row">
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Query Name*</Label>
                                    <TextField
                                        fullWidth
                                        variant="outlined"
                                        label="Query Name"
                                        name="reportName"
                                        value={objValues?.reportName || ""}
                                        onChange={handleInputChange}
                                    />
                                </FormGroup>
                            </div>

                            <div className="row">
                                <div className="form-group  col-md-6">
                                    <FormGroup>
                                        <Typography
                                            variant="subtitle2"
                                            style={{
                                                marginBottom: 8,
                                                fontWeight: 600
                                            }}
                                        >
                                            Custom Query
                                        </Typography>
                                        <TextField
                                            fullWidth
                                            multiline
                                            rows={18}
                                            variant="outlined"
                                            label="Custom Query"
                                            name="query"
                                            value={objValues?.query || ""}
                                            onChange={handleInputChange}
                                        />
                                    </FormGroup>
                                </div>

                                <div className="form-group  col-md-6">
                                    <FormGroup>
                                        <Typography
                                            variant="subtitle2"
                                            style={{
                                                marginBottom: 8,
                                                fontWeight: 600
                                            }}
                                        >
                                            Query Parameters
                                        </Typography>
                                        <ScrollableDiv listOfParams={listOfParams} objValues={objValues} facilityData={facilities} onData={onData}

                                        />
                                    </FormGroup>
                                </div>
                                <br />
                                <div className="row">
                                    <div className="mb-3 col-md-2">
                                        <Button
                                            content="Analyze"
                                            icon="search"
                                            labelPosition="left"
                                            style={{
                                                backgroundColor: "#014D88",
                                                color: "#fff"
                                            }}
                                            onClick={handleAnalyze}
                                        />
                                    </div>
                                    <div className="mb-3 col-md-2">
                                        <Button
                                            content="Dry Run"
                                            icon="play"
                                            labelPosition="left"
                                            style={{
                                                backgroundColor: "#000",
                                                color: "#fff"
                                            }}
                                            onClick={handleDryRun}
                                        />
                                    </div>
                                    <div className="mb-3 col-md-3">
                                        <Button
                                            content="Save Query"
                                            icon="save"
                                            labelPosition="left"
                                            style={{
                                                backgroundColor: "#1976D2",
                                                color: "#fff"
                                            }}
                                            onClick={handleSaveCustomReport}
                                        />
                                    </div>

                                    <div className="mb-3 col-md-2" >
                                        <Button
                                            content="Generate Report"
                                            icon="download"
                                            labelPosition="left"
                                            style={{
                                                backgroundColor: "#008000",
                                                color: "#fff"
                                            }}
                                            onClick={handleSubmit}
                                        />
                                    </div>
                                </div>


                                <Paper
                                    elevation={2}
                                    style={{
                                        padding: 24,
                                        borderRadius: 12,
                                        backgroundColor: "#FAFAFA"
                                    }}
                                >
                                    {loading && (
                                        <Message
                                            info
                                            icon
                                            style={{
                                                marginTop: 20,
                                                borderRadius: 10
                                            }}
                                        >
                                            <Message.Content>
                                                <ProgressComponent />
                                            </Message.Content>
                                        </Message>
                                    )}
                                </Paper>
                            </div>
                        </div>
                    </>

                </CardBody>
            </Card>
        </>
    );
};

export default CustomReport