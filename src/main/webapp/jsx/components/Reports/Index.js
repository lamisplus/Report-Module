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

                <form >
                    <div className="row">
                    
                        <br/>
                        <br/>
                        <div className="col-md-3 col-sm-3 col-lg-3 float-start">
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
                                active={activeItem === 'prep'}
                                onClick={()=>handleItemClick('prep')}
                                style={{backgroundColor:activeItem === 'prep' ? '#000': ""}}
                            >
                            {/* <Label>2</Label> */}
                            <span style={{color:'#fff'}}>PREP CONVERTER</span>
                            
                            </Menu.Item>
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'art'}
                                onClick={()=>handleItemClick('art')}
                                style={{backgroundColor:activeItem === 'art' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}>ART SUMMARY REPORT</span>
                               
                                {/* <Label color='teal'>3</Label> */}
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
                                name='spam'
                                active={activeItem === 'devolve'}
                                onClick={()=>handleItemClick('devolve')}
                                style={{backgroundColor:activeItem === 'devolve' ? '#000': ""}}
                            >
                            {/* <Label>4</Label> */}
                            <span style={{color:'#fff'}}>DEVOLVE REPORT</span>
                           
                            </Menu.Item>
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'gap'}
                                onClick={()=>handleItemClick('gap')}
                                style={{backgroundColor:activeItem === 'gap' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}>GAP ANALYZER</span>
                                
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
                            <Menu.Item
                                name='inbox'
                                active={activeItem === 'ict'}
                                onClick={()=>handleItemClick('ict')}
                                style={{backgroundColor:activeItem === 'ict' ? '#000': ""}}
                            >               
                                <span style={{color:'#fff'}}>ICT TRACKING</span>
                               
                            {/* <Label color='teal'>5</Label> */}
                            </Menu.Item>
                        </Menu>
                        </div>
                        <div className="col-md-9 col-sm-9 col-lg-9 float-start" style={{ backgroundColor:"#fff"}}>
                        {activeItem==='line-list' && (<PatientLineList handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}     
                        {activeItem==='appointment' && (<Appointment handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {activeItem==='radet' && (<Radet handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}     
                       {/* {activeItem==='indexing' && (<IndexingContactTracing handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {activeItem==='recency-testing' && (<RecencyTesting  handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)}
                        {activeItem==='others' && (<Others handleItemClick={handleItemClick} setCompleted={setCompleted} completed={completed}/>)} */}
                            
                        </div>                                   
                    </div>

                
                    </form>
                                             
        </>
    );
};

export default Reports