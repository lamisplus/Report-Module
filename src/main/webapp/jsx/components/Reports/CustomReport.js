import React, {useCallback, useEffect, useState, forwardRef } from "react";
import axios from "axios";
import { FormGroup, Label, CardBody, Spinner, Input, Form } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { Card } from "@material-ui/core";
import { FaDownload, FaEye, FaPlus, FaUpload } from "react-icons/fa";
import { MdModeEdit, MdPerson, MdDelete } from "react-icons/md";
import { token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button } from 'semantic-ui-react'
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import { Message, Icon, TextArea, Dropdown } from 'semantic-ui-react'
import ScrollableDiv from "../Shared/Scrollable"

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

let rowNum = 0;

const CustomReport = (props) => {
    let currentDate = new Date().toISOString().split('T')[0]
    const classes = useStyles();
    const [loading, setLoading] = useState(false)
    const [listOfParams, setListOfParams] = useState([]);
    const [facilities, setFacilities] = useState([]);
    const [listOfReport, setListOfReport] = useState([]);
    const [selectedReport, setSelectedReport] = useState("")
    const [customQuery, setCustomQuery] = useState("")
    const [customDataFields, setCustomDataFields] = useState({});
    const [modal, setModal] = useState(false);
    const [objValues, setObjValues] = useState({
        query: "",
        reportName: "",
        organisationUnitId: "",
        organisationUnitName: "",
        currentDate: currentDate
    })
    // const [objValues, setObjValues] = useState({
    //     query: "",
    //     reportName: ""
    // })
    const [formData, setFormData] = useState(objValues)

    const loadFacilities = useCallback(async () => {
        try{
            const response = await axios.get(
                `${baseUrl}v1/account`,
                {headers: { "Authorization": `Bearer ${token}` }}
            );
            setFacilities(response.data.applicationUserOrganisationUnits);
        }catch(e){
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
        // const objValuesWithTemplateStrings = containsTemplateStrings(objValues?.queryBody);
        // const text = extractPatterns(objValues?.queryBody)
    }

    const handleDryRun = (e) => {
        e.preventDefault();
        var customQuery = objValues?.query;
        customQuery = replaceValues(customQuery, customDataFields);
        customQuery = customQuery.trim().concat(" LIMIT 5");
        setCustomQuery(customQuery);
        console.log(customQuery);

        axios
            .post(`${baseUrl}customized-reports/generate-report?query=${encodeURIComponent(customQuery)}&reportName=${encodeURIComponent(objValues.reportName)}`, { headers: { "Authorization": `Bearer ${token}` } })
            .then(response => {
                // setObjValues(null)
                // props.toggleModal()
                // props.fetchNotificationConfigs()
                getCustomReports();
                toast.success("Custom report successfully generated...")
                
            })
            .catch(error => {
                console.log(error)
            }

            );

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
                // setObjValues(null)
                // props.toggleModal()
                // props.fetchNotificationConfigs()
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
                // setObjValues(objValues?.reportName, objValues?.query)
            })
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true)
        axios.get(`${baseUrl}reporting/pharmacy/${objValues.organisationUnitId}`,
            { headers: { "Authorization": `Bearer ${token}` }, responseType: 'blob' },
        )
            .then(response => {
                setLoading(false)
                const fileName = `${objValues.organisationUnitName} Pharmacy ${currentDate}`
                const responseData = response.data
                let blob = new Blob([responseData], { type: "application/octet-stream" });
                FileSaver.saveAs(blob, `${fileName}.xlsx`);
                //toast.success(" Save successful");
                //props.setActiveContent('recent-history')

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

    const openReportDeletionModal = (row) => {
        axios
            .delete(`${baseUrl}customized-reports/${row}, objValues`, {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                setModal(false)
                getCustomReports();
                toast.success("Report successfully deleted...")
            })
            .catch(error => {
                console.log(error)
            }

            );
    }

    const deleteReportModal = (row) => {
        rowNum = row.value;

        setModal(true);

    };
    function actionItems(row) {
        return [
            {
                type: 'link',
                name: 'View',
                icon: <FaEye size="22" />,
                to: {}
            },
            {
                type: 'button',
                name: 'Edit',
                icon: <MdModeEdit size="20" color='#014d88' />,
                // onClick: (() => openNotificationConfigCreationModal(row))
            },
            {
                type: 'button',
                name: 'Delete',
                icon: <MdDelete size="20" color='#014d88' />,
                onClick: (() => deleteReportModal(row))
            }
        ]
    }
    
    return (
        <>

            <Card >
                <CardBody>

                    <h2 style={{ color: '#000' }}>CUSTOM REPORT</h2>
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
                    <form >
                        <div className="row">
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Query Name*</Label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="reportName"
                                        id="reportName"
                                        onChange={handleInputChange}
                                        value={objValues?.reportName}
                                        style={{ border: "1px solid #014D88", borderRadius: "0.2rem" }}
                                    />
                                </FormGroup>
                            </div>

                            <div className="row">
                                <div className="form-group  col-md-6">
                                    <FormGroup>
                                        <Label>Custom Query*</Label>
                                        <TextArea
                                            id="query"
                                            name="query"
                                            multiline="multiline"
                                            rows={20}
                                            onChange={handleInputChange}
                                            style={{ border: "1px solid #014D88", borderRadius: "0.2rem", width: "100%" }}
                                            value={objValues?.query}
                                            className="w-100"
                                            width={100}

                                        />
                                    </FormGroup>
                                </div>

                                <div className="form-group  col-md-6">
                                    <FormGroup>
                                        <Label>Query Parameters *</Label>
                                        <ScrollableDiv listOfParams={listOfParams} objValues={objValues} facilityData={facilities} onData={onData}

                                        />
                                    </FormGroup>
                                </div>
                                <br />
                                <div className="row">
                                    {/* <div className="mb-3 col-md-2">
                                        <Button type="submit" content='Cancel' icon='right arrow' labelPosition='right' style={{ backgroundColor: "#FF0000", color: '#fff' }} onClick={handleCancel} />
                                    </div> */}
                                    <div className="mb-3 col-md-2">
                                        <Button type="submit" content='Analyze' icon='up arrow' labelPosition='right' style={{ backgroundColor: "#014d88", color: '#fff' }} onClick={handleAnalyze} />
                                    </div>
                                    <div className="mb-3 col-md-3">
                                        <Button type="submit" content='Dry Run' icon='up arrow' labelPosition='right' style={{ backgroundColor: "#008000", color: '#fff' }} onClick={handleDryRun} />
                                    </div>
                                    <div className="mb-3 col-md-3">
                                        <Button type="submit" content='Save Query' icon='up arrow' labelPosition='right' style={{ backgroundColor: "#008000", color: '#fff' }} onClick={handleSaveCustomReport} />
                                    </div>

                                    <div className="mb-3 col-md-2" >
                                        <Button type="submit" content='Generate' icon='right arrow' labelPosition='right' style={{ backgroundColor: "#008000", color: '#fff' }} onClick={handleSaveCustomReport} hidden={objValues.organisationUnitId === "" ? true : false} />
                                    </div>
                                </div>

                                {loading && (
                                    <Message icon>
                                        <Icon name='circle notched' loading />
                                        <Message.Content>
                                            <Message.Header>Just one second</Message.Header>
                                            We are fetching that content for you.
                                        </Message.Content>
                                    </Message>
                                )}
                            </div>
                        </div>
                    </form>

                </CardBody>
            </Card>
        </>
    );
};

export default CustomReport