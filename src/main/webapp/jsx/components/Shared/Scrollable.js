import { Input } from '@material-ui/core';
import React from 'react';

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
    width: '300px',
    margin: '0 auto'
  };

  const scrollableDivStyle = {
    height: '200px',
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
          <div
            key={index}
            style={{
              ...itemStyle,
              borderBottom: index === match.length - 1 ? 'none' : '1px solid #ddd',
              display:"flex"
            }}
          >
            <p>
            {Object.keys(match)[0]} :
            </p>
            <Input />
          </div>
          
          ))}
      </div>
    </div>
  );
};

export default ScrollableDiv;
