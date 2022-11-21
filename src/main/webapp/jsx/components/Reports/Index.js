import React, { useState} from "react";
import {Card, CardBody} from "reactstrap";
import {makeStyles} from "@material-ui/core/styles";
import {ToastContainer, toast} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import 'react-phone-input-2/lib/style.css'
import { Icon, Menu } from 'semantic-ui-react'
import 'semantic-ui-css/semantic.min.css';
import PatientLineList from './PatientLineList';
import Appointment from './Appointment'
import Radet from './Radet'
import BiometricReport from './BiometricReport'
import PharmacyReport from './PharmacyReport'

const useStyles = makeStyles((theme) => ({

    error:{
        color: '#f85032',
        fontSize: '12.8px'
    },  
    success: {
        color: "#4BB543 ",
        fontSize: "11px",
    },
}));


const Reports = (props) => {
    const classes = useStyles();
    const [saving, setSaving] = useState(false);
    const [activeItem, setactiveItem] = useState('basic');
    const [completed, setCompleted] = useState([]);
    const handleItemClick =(activeItem)=>{
        setactiveItem(activeItem)
        //setCompleted({...completed, ...completedMenu})
    }


    return (
        <>
            <ToastContainer autoClose={3000} hideProgressBar />
            <Card >
                <CardBody>
                    <div className="row">
                    <form >
                        <br/>
                        <br/>
                        <div className="col-md-3 float-start">
                        <Menu  size='small'  vertical  style={{backgroundColor:"#014D88"}}>
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'radet'}
                                onClick={()=>handleItemClick('radet')}
                                style={{backgroundColor:activeItem === 'radet' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}> RADET REPORT</span>
                                
                            </Menu.Item>
                            
                            <Menu.Item
                                name='spam'
                                active={activeItem === 'appointment'}
                                onClick={()=>handleItemClick('appointment')}
                                style={{backgroundColor:activeItem === 'appointment' ? '#000': ""}}
                            >
                            {/* <Label>4</Label> */}
                            <span style={{color:'#fff'}}>APPOINTMENT REPORT</span>
                            
                            </Menu.Item>
                            <Menu.Item
                                name='spam'
                                active={activeItem === 'line-list'}
                                onClick={()=>handleItemClick('line-list')}
                                style={{backgroundColor:activeItem === 'line-list' ? '#000': ""}}
                            >
                            {/* <Label>4</Label> */}
                            <span style={{color:'#fff'}}>PATIENT LINE LIST</span>
                           
                            </Menu.Item>
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'pharmacy-report'}
                                onClick={()=>handleItemClick('pharmacy-report')}
                                style={{backgroundColor:activeItem === 'pharmacy-report' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}>PHARMACY REPORT</span>
                                
                            {/* <Label color='teal'>5</Label> */}
                            </Menu.Item>
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'biometric'}
                                onClick={()=>handleItemClick('biometric')}
                                style={{backgroundColor:activeItem === 'biometric' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}>BIOMETRIC REPORT</span>
                                
                            {/* <Label color='teal'>5</Label> */}
                            </Menu.Item>
                            
                        </Menu>
                        </div>
                        <div className="col-md-9 float-end" style={{ backgroundColor:"#fff"}}>
                        {activeItem==='line-list' && (<PatientLineList handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}     
                        {activeItem==='appointment' && (<Appointment handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {activeItem==='radet' && (<Radet handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}     
                        {activeItem==='biometric' && (<BiometricReport handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {activeItem==='pharmacy-report' && (<PharmacyReport  handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {/* {activeItem==='others' && (<Others handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)} */}
                            
                        </div>
                        </form>                                   
                    </div>
            </CardBody>
            </Card>                                 
        </>
    );
};

export default Reports