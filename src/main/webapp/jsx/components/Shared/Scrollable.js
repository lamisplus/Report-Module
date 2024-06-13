import React from 'react';
import {FormGroup, Label, CardBody, Spinner,Input,Form} from "reactstrap";
// import { Input } from '@material-ui/core';

const ScrollableDiv = (props) => {
  const items = Array.from({ length: 50 }, (_, index) => `Item ${index + 1}`);


  function extractPatterns(e) {
    const pattern = /\{\{([^}]+)\}\}/g;
    let match;
    while ((match = pattern.exec(e)) !== null) {
      console.log(match[1]);
    }
  }

  const containerStyle = {
    width: 'auto',
    margin: '0 auto'
  };

  const scrollableDivStyle = {
    height: '325px',
    overflowY: 'auto',
    border: '1px solid #ccc',
    padding: '10px',
    boxSizing: 'border-box'
  };

  const itemStyle = {
    padding: '5px',
    borderBottom: '1px solid #ddd'
  };

  return (
    <div style={containerStyle}>
      <div style={scrollableDivStyle}>
        {props.listOfParams?.map((match, index) => (
          <div key={index} style={{...itemStyle, display:"flex"}}>
            <FormGroup>
            <Label>{Object.keys(match)[0]}</Label>
            if({Object.keys(match)[0]}.includes("Facility")){
              <select
                  className="form-control"
                  name="organisationUnitId"
                  id="organisationUnitId"
                  value={props.objValues?.organisationUnitId}
                  style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}>
                  <option value={""}></option>
                  {props.objValues?.facilities.map((value) => (
                      <option key={value.id} value={value.organisationUnitId}>
                          {value.organisationUnitName}
                      </option>
                  ))}
              </select>
            }else if({Object.keys(match)[0]}.includes("Date")){
              <Input
                type="date"
                className="form-control"
                name="endDate"
                id="endDate"
                min={"1980-01-01"}
                max={props.objValues?.currentDate}
                style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
              />
            }else{
              <Input
                type="date"
                className="form-control"
                name="endDate"
                id="endDate"
                min={"1980-01-01"}
                max={props.objValues?.currentDate}
                style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
              />
            }
            </FormGroup>
          </div>
          
          ))}
      </div>
    </div>
  );
};

export default ScrollableDiv;
