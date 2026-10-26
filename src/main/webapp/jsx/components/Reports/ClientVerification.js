import React, { useEffect, useState } from "react";
import axios from "axios";
import FileSaver from "file-saver";
import { toast } from "react-toastify";

import { CardBody } from "reactstrap";

import {
    Card,
    Paper,
    Box,
    Typography,
    TextField
} from "@material-ui/core";

import Autocomplete from "@material-ui/lab/Autocomplete";

import VerifiedUserIcon from "@material-ui/icons/VerifiedUser";
import BusinessIcon from "@material-ui/icons/Business";

import { Button, Message } from "semantic-ui-react";

import { token, url as baseUrl } from "../../../api";
import ProgressComponent from "./ProgressComponent";

const ClientVerification = () => {
    const currentDate = new Date().toISOString().split("T")[0];

    const [loading, setLoading] = useState(false);
    const [facilities, setFacilities] = useState([]);

    const [objValues, setObjValues] = useState({
        organisationUnitId: "",
        organisationUnitName: ""
    });

    useEffect(() => {
        loadFacilities();
    }, []);

    const loadFacilities = () => {
        axios
            .get(`${baseUrl}account`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then((response) => {
                setFacilities(
                    response.data.applicationUserOrganisationUnits || []
                );
            })
            .catch(() => {
                toast.error("Unable to load facilities");
            });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        setLoading(true);

        axios
            .get(
                `${baseUrl}reporting/client-service-list/${objValues.organisationUnitId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    },
                    responseType: "blob"
                }
            )
            .then((response) => {
                setLoading(false);

                const blob = new Blob([response.data], {
                    type:
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                });

                const fileName = `${objValues.organisationUnitName}_CLIENT_VERIFICATION_${currentDate}.xlsx`;

                FileSaver.saveAs(blob, fileName);

                toast.success(
                    "Client Verification Report generated successfully"
                );
            })
            .catch((error) => {
                setLoading(false);

                if (error.response?.data?.apierror?.message) {
                    toast.error(
                        error.response.data.apierror.message
                    );
                } else {
                    toast.error(
                        "Something went wrong while generating report."
                    );
                }
            });
    };

    return (
        <Card>
            <CardBody>

                {/* HEADER */}
                <Box mb={3}>
                    <Typography
                        variant="h5"
                        style={{
                            color: "#014D88",
                            fontWeight: 600,
                            display: "flex",
                            alignItems: "center"
                        }}
                    >
                        <VerifiedUserIcon
                            style={{
                                marginRight: 10,
                                color: "#014D88"
                            }}
                        />
                        Client Verification Report
                    </Typography>

                    <Typography
                        variant="body2"
                        color="textSecondary"
                    >
                        Generate client verification reports
                        for a selected facility and export
                        results to Excel.
                    </Typography>
                </Box>

                {/* FORM */}
                <Paper
                    elevation={2}
                    style={{
                        padding: 24,
                        borderRadius: 12,
                        backgroundColor: "#FAFAFA"
                    }}
                >
                    <div className="row">

                        {/* FACILITY */}
                        <div className="col-md-12 mb-4">
                            <Typography
                                variant="subtitle2"
                                style={{
                                    marginBottom: 8,
                                    fontWeight: 600,
                                    display: "flex",
                                    alignItems: "center"
                                }}
                            >
                                <BusinessIcon
                                    fontSize="small"
                                    style={{
                                        marginRight: 8,
                                        color: "#014D88"
                                    }}
                                />
                                Facility
                            </Typography>

                            <Autocomplete
                                options={facilities}
                                getOptionLabel={(option) =>
                                    option?.organisationUnitName || ""
                                }
                                value={
                                    facilities.find(
                                        (facility) =>
                                            facility.organisationUnitId ===
                                            objValues.organisationUnitId
                                    ) || null
                                }
                                onChange={(event, value) => {
                                    setObjValues({
                                        ...objValues,
                                        organisationUnitId:
                                            value?.organisationUnitId || "",
                                        organisationUnitName:
                                            value?.organisationUnitName || ""
                                    });
                                }}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        variant="outlined"
                                        fullWidth
                                        placeholder="Search and select facility..."
                                        helperText="Select facility"
                                    />
                                )}
                            />
                        </div>

                        {/* SUBMIT */}
                        <div className="col-md-12">
                            <Button
                                primary
                                icon="download"
                                labelPosition="left"
                                content={
                                    loading
                                        ? "Generating..."
                                        : "Generate Report"
                                }
                                style={{
                                    backgroundColor: "#014D88"
                                }}
                                onClick={handleSubmit}
                                disabled={
                                    !objValues.organisationUnitId ||
                                    loading
                                }
                            />
                        </div>

                    </div>
                </Paper>

                {/* LOADING */}
                {loading && (
                    <Message
                        info
                        icon
                        style={{
                            marginTop: 20,
                            borderRadius: 10
                        }}
                    >
                        <Message.Content>
                            <ProgressComponent />
                        </Message.Content>
                    </Message>
                )}

            </CardBody>
        </Card>
    );
};

export default ClientVerification;