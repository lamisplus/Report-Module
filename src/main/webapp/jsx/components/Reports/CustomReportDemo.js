import React, { useEffect, useState } from "react";
import axios from "axios";
import { makeStyles } from "@material-ui/core/styles";
import { token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button } from 'semantic-ui-react'
import { toast } from "react-toastify";
import FileSaver from "file-saver";
import ProgressComponent from "./ProgressComponent";

const SOCKET_URL = 'http://localhost:8080/ws-chat/';
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


const CustomReportDemo = (props) => {
    let currentDate = new Date().toISOString().split('T')[0]
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

    const handleProgressReport = (e) => {
        e.preventDefault()

        const query = "select * from patient_person"
        const reportName = "Patient_Person_Report"
        axios
            .get(`${baseUrl}customized-reports/generate-report?query=${query}&reportName=${reportName}`,
                { headers: { "Authorization": `Bearer ${token}` } }
            )
            .then((response) => {
                const fileName = `${reportName} ${currentDate}`
                const responseData = response.data
                let blob = new Blob([responseData], {type: "application/octet-stream"});
                FileSaver.saveAs(blob, `${fileName}.xlsx`);
            })
            .catch((error) => {
                //console.log(error);
            });
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


    return (
        <>
            <div>
                <ProgressComponent/>
            </div>
            <div className="form-group mb-3 col-md-6">
                <Button type="button" content='Generate Report' icon='right arrow' labelPosition='right'
                        style={{backgroundColor: "#014d88", color: '#fff'}} onClick={handleProgressReport}
                        />
            </div>
        </>
    );
};
export default CustomReportDemo
