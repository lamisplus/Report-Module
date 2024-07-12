import React, {useState} from 'react'
import SockJsClient from 'react-stomp';
import {wsUrl} from "../../../api";

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