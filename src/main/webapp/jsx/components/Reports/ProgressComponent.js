import React, { useState } from 'react';
import SockJsClient from 'react-stomp';
import { wsUrl } from "../../../api";

function ProgressComponent() {
    const [message, setMessage] = useState("");

    const onConnected = () => {
        console.log("Connected!!");
    };

    const onMessageReceived = (msg) => {
        console.log("Message received: ", msg);
        if (msg) {
            setMessage(msg);
        }
    };

    const onDisconnected = () => {
        console.log("Disconnected!");
    };

    return (
        <div>
            <SockJsClient
                url={wsUrl}
                topics={['/topic/report-generation-progress']}
                onConnect={onConnected}
                onDisconnect={onDisconnected}
                onMessage={msg => onMessageReceived(msg)}
                debug={true}  // Enable debugging
            />
            <div><h3>{message}</h3></div>
        </div>
    );
}

export default ProgressComponent;
