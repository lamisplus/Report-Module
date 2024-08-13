import React,  {useState, useEffect, useRef} from 'react'
import SockJsClient from 'react-stomp';
import {wsUrl} from "../../../api";

import { LinearProgress, Box, Typography } from '@mui/material'; // Assuming you're using Material-UI
// import { useEffect } from 'react';


// function ProgressComponent() {
//     const [progress, setProgress] = useState(0);
//     const [message, setMessage] = useState("");
//     let onConnected = () => {
//         console.log("Connected!!")
//     }
//     let onMessageReceived = (msg) => {
//         console.log(msg)
//         if (msg) {
//             setMessage(msg);
//         }

//     }
//     let onDisconnected = () => {
//         console.log("Disconnected!");
//     }

//     useEffect(() => {
//         const interval = setInterval(() => {
//             setProgress((prevProgress) => {
//                 if (prevProgress >= 100) {
//                     clearInterval(interval); // Stop the interval when progress reaches 100%
//                     return 100;
//                 } else {
//                     return prevProgress + 1; // Increment progress by 1
//                 }
//             });
//         }, 100); // Adjust the interval time to control speed (100ms here means 1% per 100ms)

//         return () => {
//             clearInterval(interval);
//         };
//     }, []);


//     // onMessageReceived(message.setInterval)

//     return (
        
//         <Box sx={{ width: '100%', mt: 2 }}>
//             <div>
//             <SockJsClient
//                 url={wsUrl}
//                 topics={['topic/report-generation-progress']}
//                 onConnect={onConnected}
//                 onDisconnect={onDisconnected}
//                 onMessage={msg => onMessageReceived(msg)}
//                 debug={false}
//             />
//             <div><h3>{message}</h3></div>
//             <div><h3>{message}</h3></div>
//             {console.log(message)}

//         </div>
//             <LinearProgress variant="determinate" value={progress} />
//             <Typography variant="body2" color="textSecondary">{progress}%</Typography>
//         </Box>
//     );
// }

function ProgressComponent() {
    const [message, setMessage] = useState("");
    let onConnected = () => {
        console.log("Connected!!")
    }
    let onMessageReceived = (msg) => {
        console.log(msg)
        if (msg) {
            setMessage(msg);
        }

    }
    let onDisconnected = () => {
        console.log("Disconnected!");
    }
    return (
        <div>
            <SockJsClient
                url={wsUrl}
                topics={['/topic/report-generation-progress']}
                onConnect={onConnected}
                onDisconnect={onDisconnected}
                onMessage={msg => onMessageReceived(msg)}
                debug={false}
            />
            <div><h3>{message}</h3></div>
        </div>
    );
}

export default ProgressComponent