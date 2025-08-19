import React from 'react';
import { Card, CardBody, CardText } from 'reactstrap';
import BlockIcon from '@mui/icons-material/Block';

const NoAccessCard = ({ title = "No Report Access", message = "You do not have permission to view any reports. Contact your administrator if you believe this is an error." }) => {
    return (
        <div className="no-access-container">
            <Card className="no-access-card">
                <CardBody className="text-center p-5">
                    <div className="no-access-icon mb-3">
                        <BlockIcon fontSize="large" style={{ color: '#d9534f' }} />
                    </div>
                    <h4 className="text-primary mb-2">{title}</h4>
                    <CardText className="text-muted mb-0">
                        {message}
                    </CardText>
                </CardBody>
            </Card>
        </div>
    );
};

export default NoAccessCard;