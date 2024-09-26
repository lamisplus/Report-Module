import React, {useState} from 'react';
import {Label, Input} from "reactstrap";
// import { Input } from '@material-ui/core';

const ScrollableDiv = (props) => {
  const items = Array.from({ length: 50 }, (_, index) => `Item ${index + 1}`);
  const [parameterFormFields, setParameterFormFields] = useState({});

  props.onData(parameterFormFields);
  const handleChange =(e) => {
    const {name, value} = e.target;
    setParameterFormFields({
      ...parameterFormFields,
      [name]: value,
    });
    props.onData(parameterFormFields);
  };

  const handleParameterSubmission =(e) => {
    e.preventDefault();
    props.onData(parameterFormFields);
  };

  function extractPatterns(e) {
    const pattern = /\{\{([^}]+)\}\}/g;
    let match;
    while ((match = pattern.exec(e)) !== null) {
      console.log(match[1]);
    }
  }

  function formatComponentName(value){
    return value.toLowerCase().replace(" ", "_");
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
    padding: '2px',
    // borderBottom: '1px solid #ddd'
  };

  return (
    <div style={containerStyle}>
      <div style={scrollableDivStyle}>
        {props.listOfParams?.map((match, index) => (
          <div key={index} style={{...itemStyle}}>
            <form id="parameterForm" onSubmit={handleParameterSubmission}>
              <div class="row col-md-12">
                <div class="mb-2 col-md-4">
                  <Label>{Object.keys(match)[0]}</Label>
                </div>
                <div class="mb-2 col-md-8">
                {Object.keys(match)[0].includes("Facility") ?
                  <select
                      className="form-control"
                      name={formatComponentName(Object.keys(match)[0])}
                      id={formatComponentName(Object.keys(match)[0])}
                      value={parameterFormFields[formatComponentName(Object.keys(match)[0])]}
                      style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                      onChange={handleChange}>
                        <option value={"0"}>Select a Facility</option>
                          {props.facilityData.map((value) => (
                              <option key={value.id} value={value.organisationUnitId}>
                                  {value.organisationUnitName}
                              </option>
                          ))}
                  </select> 
                  : (Object.keys(match)[0].includes("Date") ?
                  <Input
                    type="date"
                    className="form-control"
                    name={formatComponentName(Object.keys(match)[0])}
                    id={formatComponentName(Object.keys(match)[0])}
                    min={"1980-01-01"}
                    max={props.objValues?.currentDate}
                    style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                    value={parameterFormFields[formatComponentName(Object.keys(match)[0])]}
                    onChange={handleChange}
                  /> :
                  <Input
                    type="text"
                    className="form-control"
                    name={formatComponentName(Object.keys(match)[0])}
                    id={formatComponentName(Object.keys(match)[0])}
                    style={{border: "1px solid #014D88", borderRadius:"0.2rem"}}
                    value={parameterFormFields[formatComponentName(Object.keys(match)[0])]}
                    onChange={handleChange}
                  />
                )}
                </div>
              </div>
            </form>
          </div>
          ))}
      </div>
    </div>
  );
};

export default ScrollableDiv;
