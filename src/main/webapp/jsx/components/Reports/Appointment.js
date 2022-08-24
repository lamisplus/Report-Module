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
import MaterialTable from 'material-table';
import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';
import 'react-toastify/dist/ReactToastify.css';
import 'react-widgets/dist/css/react-widgets.css';
import { forwardRef } from 'react';
import { toast} from "react-toastify";
import { Message, Icon } from 'semantic-ui-react'

const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
    Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
    Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
    DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
    Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
    Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
    FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
    LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
    NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
    ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
    SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
    ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
    ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
    };
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
    const [loading, setLoading] = useState(false)
    const [showContent, setShowContent] = useState(false)
    const [appointmentReport, setAppointmentReport]= useState([])
    const [facilities, setFacilities] = useState([]);
    const [tableTitle, setTableTitle]= useState("");
    const [showNoRecord, setShowNoRecord]= useState(false)
    const [objValues, setObjValues]=useState({       
        facilityId:"", startDate:"", endDate:"", type:""
    })
    const handleSubmit = (e) => {        
        e.preventDefault();
        setLoading(true)
        axios.get(`${baseUrl}reporting/${objValues.type}?facilityId=${objValues.facilityId}&startDate=${objValues.startDate}&endDate=${objValues.endDate} `,
           { headers: {"Authorization" : `Bearer ${token}`}},
          
          )
              .then(response => {                
                setLoading(false)
                setShowContent(true)
                if(objValues.type==='miss-refill'){
                    setTableTitle(`Missed Refill Appointment  -  From :${objValues.startDate}  - To: ${objValues.endDate}`)
                }else if(objValues.type==='miss-clinic'){
                    setTableTitle(`Missed Clinic Appointment   -  From :${objValues.startDate}  - To: ${objValues.endDate}`)
                }else if(objValues.type==='clinic-appointment'){
                    setTableTitle(`Clinic Appointment   -  From :${objValues.startDate}  - To: ${objValues.endDate}`)
                }else if(objValues.type==='refill-appointment'){
                    setTableTitle(`Refill Appointment   -  From :${objValues.startDate}  - To: ${objValues.endDate}`)
                }else{
                    setTableTitle('')
                }
                setAppointmentReport(response.data)
                if(response.data.length <=0){
                    setShowNoRecord(true)
                }else{
                    setShowNoRecord(false)
                }
              })
              .catch(error => {
                setLoading(false)
                setShowContent(false)
                if(error.response && error.response.data){
                    let errorMessage = error.response.data.apierror && error.response.data.apierror.message!=="" ? error.response.data.apierror.message :  "Something went wrong, please try again";
                    toast.error(errorMessage);
                  }
                  else{
                    toast.error("Something went wrong. Please try again...");
                  }
              });
            

    }
    const handleInputChange = e => {
        setObjValues ({...objValues,  [e.target.name]: e.target.value});
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
                                    <input
                                        type="date"
                                        className="form-control"
                                        name="startDate"
                                        id="startDate"
                                        onChange={handleInputChange}
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    />
                                    
                                </FormGroup>
                            </div>
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>End Date*</Label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        name="endDate"
                                        id="endDate"
                                        onChange={handleInputChange}
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    />
                                     
                                    
                                </FormGroup>
                            </div>
                            <div className="form-group  col-md-6">
                                <FormGroup>
                                    <Label>Facility*</Label>
                                    <select
                                        className="form-control"
                                        name="facilityId"
                                        id="facilityId"
                                        onChange={handleInputChange}
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
                                        name="type"
                                        id="type"
                                        onChange={handleInputChange}
                                        style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                                    >
                                        <option value={""}></option>
                                        <option value="miss-refill">Missed Refill Appointment</option>
                                        <option value="miss-clinic">Missed Clinic Appointment </option>
                                        <option value="clinic-appointment">Scheduled Clinic Visit(Clinic Appointment)</option>
                                        <option value="refill-appointment">Scheduled Refill Visit(Refill Appointment)</option>
                                        
                                    </select>
                                    
                                </FormGroup>
                            </div>
                            <br />
                            <div className="row">
                            <div className="form-group mb-3 col-md-6">
                            <Button type="submit" content='Generate Report' icon='right arrow' labelPosition='right' style={{backgroundColor:"#014d88", color:'#fff'}} onClick={handleSubmit}
                                disabled={objValues.facilityId==="" || objValues.startDate==="" || objValues.endDate===""  || objValues.type==="" ? true : false}
                            />
                            </div>
                            </div>
                        </div>
                    </form>

                    <br/>
                    {loading && (
                        <Message icon>
                            <Icon name='circle notched' loading />
                            <Message.Content>
                            <Message.Header>Just one second</Message.Header>
                            We are fetching that content for you.
                            </Message.Content>
                        </Message>
                    )}
                    {showNoRecord && (
                        <Message info>
                            <Message.Content>
                            {/* <Message.Header>Just one second</Message.Header> */}
                            <b>No Record Found</b>
                            </Message.Content>
                        </Message>
                    )}
                    {showContent &&(
                        <MaterialTable
                            icons={tableIcons}
                            title= {tableTitle}
                            columns={[
                                { title: "Name", field: "name" },
                                { title: "Hospital Num", field: "hospitalNum" },
                                { title: "Patient Id", field: "patientId" },                                
                                { title: "DOB", field: "dateBirth" },
                                { title: "Age", field: "age" },
                                { title: "phone", field: "phone" },
                                { title: "Art Start Date", field: "artStartDate" },
                                { title: "Date Of LastVisit", field: "dateOfLastVisit" },
                                { title: "Date Of NextVisit", field: "dateOfNextVisit" },
                                { title: "Current Status", field: "currentStatus" },
                                { title: "Case Manager", field: "caseManager" },
                                { title: "Facility Name", field: "facilityName" },
                                { title: "lga", field: "lga" },
                                { title: "Lga Of Residence", field: "lgaOfResidence" },
                                { title: "state", field: "state" },
                                { title: "State Of Residence", field: "stateOfResidence" },

                            ]}
                            isLoading={loading}
                            data={appointmentReport.map((row) => ({
                                name: row.name,
                                hospitalNum: row.hospitalNum,
                                patientId:row.patientId,
                                dateBirth: row.dateBirth,
                                age: row.age,
                                phone: row.phone,
                                artStartDate: row.artStartDate,
                                dateOfLastVisit: row.dateOfLastVisit,
                                dateOfNextVisit:row.dateOfNextVisit,
                                currentStatus: row.currentStatus,
                                caseManager: row.caseManager,
                                facilityName: row.facilityName,
                                lga:row.lga,
                                lgaOfResidence: row.lgaOfResidence,
                                state: row.state,
                                stateOfResidence:row.stateOfResidence,
                                
                                }))}
                            
                                        options={{
                                        headerStyle: {
                                            backgroundColor: "#014d88",
                                            color: "#fff",
                                        },
                                        searchFieldStyle: {
                                            width : '150%',
                                            margingLeft: '250px',
                                        },
                                        filtering: false,
                                        exportButton: true,
                                        searchFieldAlignment: 'left',
                                        pageSizeOptions:[10,20,100, 500,1000,2000,3000],
                                        pageSize:10,
                                        debounceInterval: 400
                            }}
                        />
                    )}
                </CardBody>
            </Card>                                 
        </>
    );
};

export default Appointment