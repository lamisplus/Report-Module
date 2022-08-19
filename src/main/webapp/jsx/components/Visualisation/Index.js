import React, { useState } from "react";
import {Card,CardBody,} from "reactstrap";
import { makeStyles } from "@material-ui/core/styles";
import {  Icon, Button, Breadcrumb} from 'semantic-ui-react';
import {Form,Row,Col,FormGroup,Label,Input, FormFeedback} from 'reactstrap';
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";

const useStyles = makeStyles(theme => ({
    card: {
        margin: theme.spacing(20),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    cardBottom: {
        marginBottom: 20
    },
    Select: {
        height: 45,
        width: 350
    },
    button: {
        margin: theme.spacing(1)
    },
    root: {
        '& > *': {
          margin: theme.spacing(1)
        }
    },
    input: {
        display: 'none'
    },
    error:{
        color: '#f85032',
        fontSize: '12.8px'
    }
}))

const GenerateCharts = (props) => {
    const classes = useStyles()
    const [loading, setLoading] = useState(false)


  return (
    <div >
        <Card >
         <CardBody>

                 <Row style={{ marginTop: '20px'}}>
  
                    <Col md="3">
                    <FormGroup>
                        <Label>Data Category </Label>
                            <Input
                                type="select"
                                name="category"
                                id="category"
                              
                            >
                                <option value=""></option>
                                <option value="indicator"> Indicator </option>

                            </Input>

                    </FormGroup>
                    </Col>
                    <Col md="3">
                    <FormGroup>
                        <Label>Chart Type </Label>
                            <Input
                                type="select"
                                name="chart_type"
                                id="chart_type"
                               
                            >
                                <option value=""></option>
                                <option value="column"> Pie Chart</option>
                                <option value="column"> Bar Chart</option>
                                <option value="column"> Column Chart </option>
                                <option value="column"> Line Chart</option>
                                <option value="column"> Area Chart </option>
                            </Input>

                    </FormGroup>
                    </Col>
                    <Col md="3">
                    <FormGroup>
                        <Label>Gender </Label>
                            <Input
                                type="select"
                                name="gender"
                                id="gender"
                              
                            >
                                <option value="">All</option>
                                <option value="Male">Male </option>
                                <option value="Female"> Female </option>
                               
                            </Input>

                    </FormGroup>
                    </Col>
                    <Col md="3">
                     <FormGroup>
                            <Label for="exampleSelect">Age Disaggregation</Label>
                                <Input type="text" name="age_disaggregation" id="age_disaggregation"  >
                                    
                                </Input>
                                    <FormFeedback></FormFeedback>
                        </FormGroup>
                    </Col>
                    <Col md="3">
                    <FormGroup>
                        <Label>Data Element </Label>
                            <Input
                                type="select"
                                name="data_element"
                                id="data_element"
                                
                            >

                                <option value=""></option>
                                <option value="TX_CURR">TX_CURR </option>
                                <option value="TX_NEW">TX_NEW </option>
                                <option value="PVLS_D">PVLS_D </option>
                                <option value="PVLS_N">PVLS_N </option>
                                <option value="HTS_POS">HTS_POS </option>
                                <option value="HTS">HTS </option>

                            </Input>

                    </FormGroup>
                    </Col>

                    <Col md="3">
                    <FormGroup>
                        <Label>Start Date </Label>
                        <Input
                            type="date"
                            name="data_element"
                            id="data_element"                                
                        />
                    </FormGroup>
                    </Col>
                    <Col md="3">
                    <FormGroup>
                        <Label>End Date </Label>
                        <Input
                            type="date"
                            name="data_element"
                            id="data_element"                                
                        /> 
                    </FormGroup>
                    </Col>
                   
                    <Col style={{ marginTop: '20px'}}>
                    <Button icon labelPosition='right' color='blue' >
                        Generate Chart Report
                    <Icon name='right arrow' />
                    </Button>
                    </Col>
                    </Row>
               
                 
            </CardBody>
        </Card>
    </div>
  );
}

export default GenerateCharts;



