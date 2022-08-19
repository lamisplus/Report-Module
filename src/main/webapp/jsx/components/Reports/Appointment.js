import React, {useEffect, useState} from "react";
import axios from "axios";
import {FormGroup, Label , CardBody, Spinner,Input,Form} from "reactstrap";
import {makeStyles} from "@material-ui/core/styles";
import {Card} from "@material-ui/core";
// import {Link, useHistory, useLocation} from "react-router-dom";
// import {TiArrowBack} from 'react-icons/ti'
import {token, url as baseUrl } from "../../../api";
import 'react-phone-input-2/lib/style.css'
import { Button} from 'semantic-ui-react'


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
    error:{
        color: '#f85032',
        fontSize: '12.8px'
    }
}));


const Appointment = (props) => {
    const classes = useStyles();
    const [errors, setErrors] = useState({});
    const [facilities, setFacilities] = useState([]);
    const handleItemClick =(page, completedMenu)=>{
        props.handleItemClick(page)
        if(props.completed.includes(completedMenu)) {

        }else{
            props.setCompleted([...props.completed, completedMenu])
        }
        
    }
    useEffect(() => {
        Facilities()
      }, []);
    //Get list of WhoStaging
    const Facilities =()=>{
    axios
        .get(`${baseUrl}account`,
            { headers: {"Authorization" : `Bearer ${token}`} }
        )
        .then((response) => {
            console.log(response.data);
            setFacilities(response.data.applicationUserOrganisationUnits);
        })
        .catch((error) => {
        //console.log(error);
        });
    
    }

    return (
        <>
            
            <Card >
                <CardBody>
    
                <h2 style={{color:'#000'}}>APPOINTMENT REPORT</h2>
                <br/>
                    <form >
                        <div className="row">

                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Start Date*</Label>
                                    <select
                                        className="form-control"
                                        name="sex"
                                        id="sex"
                                        
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    >
                                        <option value={""}></option>
                                        <option value={""}>Patient Data</option>
                                        
                                    </select>
                                    
                                </FormGroup>
                            </div>
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>End Date*</Label>
                                    <select
                                        className="form-control"
                                        name="sex"
                                        id="sex"
                                        
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    >
                                        <option value={""}></option>
                                        <option value={""}>Patient Data</option>
                                        
                                    </select>
                                    
                                </FormGroup>
                            </div>
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Facility*</Label>
                                    <select
                                        className="form-control"
                                        name="sex"
                                        id="sex"
                                        
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
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
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Type*</Label>
                                    <select
                                        className="form-control"
                                        name="sex"
                                        id="sex"
                                        
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    >
                                        <option value={""}></option>
                                        <option value={""}>Patient Line </option>
                                        
                                    </select>
                                    
                                </FormGroup>
                            </div>
                            <br />
                            <div className="row">
                            <div className="form-group mb-3 col-md-6">
                            <Button content='Generate Report' icon='right arrow' labelPosition='right' style={{backgroundColor:"#014d88", color:'#fff'}} onClick={()=>handleItemClick('pre-test-counsel','basic')}/>
                            </div>
                            </div>
                        </div>
                    </form>
                </CardBody>
            </Card>                                 
        </>
    );
};

export default Appointment