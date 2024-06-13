import React, { useEffect, useState } from "react";
import axios from "axios";
import { FormGroup, Label, CardBody, Spinner, Input, Form } from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import { Card } from "@material-ui/core";
// import {Link, useHistory, useLocation} from "react-router-dom";
// import {TiArrowBack} from 'react-icons/ti'
import { token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button } from 'semantic-ui-react'
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import { Message, Icon, TextArea } from 'semantic-ui-react'
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


const CustomReport = (props) => {
    let currentDate = new Date().toISOString().split('T')[0]
    const classes = useStyles();
    const [loading, setLoading] = useState(false)
    const [facilities, setFacilities] = useState([]);
    const [listOfParams, setListOfParams] = useState([]);
    const [objValues, setObjValues] = useState({
        queryBody: "",
        organisationUnitId:"",
        organisationUnitName:"",
        currentDate:currentDate,
        facilities:facilities,
        reportName: "",
    })
    useEffect(() => {
        Facilities()
    }, []);
    //Get list of WhoStaging
    const Facilities = () => {
        axios
            .get(`${baseUrl}account`,
                { headers: { "Authorization": `Bearer ${token}` } }
            )
            .then((response) => {
                //console.log(response.data);
                setFacilities(response.data.applicationUserOrganisationUnits);
            })
            .catch((error) => {
                //console.log(error);
            });

    }

    const containsTemplateStrings = (obj) => {
        return Object.values(obj).some((value) => {
            return typeof value === 'string' && value.includes('{{');
        });
    };

    const handleInputChange = e => {
        setObjValues({ ...objValues, [e.target.name]: e.target.value, query: e.target.innerText });
        containsTemplateStrings(objValues);
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
        const objValuesWithTemplateStrings = containsTemplateStrings(objValues?.queryBody);
        const text = extractPatterns(objValues?.queryBody)
    }

    const handleCancel = (e) => {
        e.preventDefault();
        const objValuesWithTemplateStrings = containsTemplateStrings(objValues?.queryBody);
        const text = extractPatterns(objValues?.queryBody)
    }

    const handleDryRun = (e) => {
        e.preventDefault();
        const objValuesWithTemplateStrings = containsTemplateStrings(objValues?.queryBody);
        const text = extractPatterns(objValues?.queryBody)
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
    //   console.log(analyzeText);
    return (
        <>

            <Card >
                <CardBody>

                    <h2 style={{ color: '#000' }}>CUSTOM REPORT</h2>
                    <br />
                    <form >
                        <div className="row">

                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Custom Query*</Label>
                                    <TextArea
                                        id="queryBody"
                                        name="queryBody"
                                        multiline="multiline"
                                        rows={20}
                                        //   inputProps={{ maxLength: 200 }}
                                        //   value={notificationObject?.messageBody}
                                        value={objValues?.queryBody}
                                        onChange={handleInputChange}
                                        style={{ border: "1px solid #014D88", borderRadius: "0.2rem", width: "100%" }}
                                        className="w-100"
                                        width={100}

                                    />
                                </FormGroup>
                            </div>

                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Query Parameters *</Label>
                                    <ScrollableDiv listOfParams={listOfParams} objValues={objValues}

                                    />
                                </FormGroup>
                            </div>
                            <br />
                            <div className="row">
                                <div className="mb-3 col-md-2">
                                    <Button type="submit" content='Cancel' icon='right arrow' labelPosition='right' style={{ backgroundColor: "#FF0000", color: '#fff' }} onClick={handleCancel} />
                                </div>
                                <div className="mb-3 col-md-2">
                                    <Button type="submit" content='Analyze' icon='up arrow' labelPosition='right' style={{ backgroundColor: "#014d88", color: '#fff' }} onClick={handleAnalyze}  />
                                </div>
                                <div className="mb-3 col-md-3">
                                    <Button type="submit" content='Dry Run' icon='up arrow' labelPosition='right' style={{ backgroundColor: "#008000", color: '#fff' }} onClick={handleDryRun}  />
                                </div>
                                <div className="mb-3 col-md-2" hidden>
                                    <Button type="submit" content='Generate' icon='right arrow' labelPosition='right' style={{ backgroundColor: "#008000", color: '#fff' }} onClick={handleSubmit} hidden={objValues.organisationUnitId === "" ? true : false} />
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
                    </form>

                </CardBody>
            </Card>
        </>
    );
};

export default CustomReport