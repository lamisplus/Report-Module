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

import BusinessIcon from "@material-ui/icons/Business";
import GroupIcon from "@material-ui/icons/Group";

import { Button, Message } from "semantic-ui-react";

import { token, url as baseUrl } from "../../../api";
import ProgressComponent from "./ProgressComponent";

const HTSIndexReport = () => {
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
            .post(
                `${baseUrl}family-index-report?facilityId=${objValues.organisationUnitId}`,
                objValues.organisationUnitId,
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

                const fileName =
                    `${objValues.organisationUnitName}_HTS_INDEX_REPORT_${currentDate}.xlsx`;

                FileSaver.saveAs(blob, fileName);

                toast.success(
                    "HTS Family Index Report generated successfully"
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
                        <GroupIcon
                            style={{
                                marginRight: 10,
                                color: "#014D88"
                            }}
                        />
                        HTS Index Report
                    </Typography>

                    <Typography
                        variant="body2"
                        color="textSecondary"
                        style={{ marginTop: 5 }}
                    >
                        Generate the HTS Index Report for a selected
                        facility.
                    </Typography>
                </Box>

                <Paper
                    elevation={2}
                    style={{
                        padding: 24,
                        borderRadius: 12,
                        backgroundColor: "#FAFAFA"
                    }}
                >
                    <div className="row">

                        {/* Facility */}
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
                                        helperText="Select the facility for which you want to generate the report"
                                    />
                                )}
                            />
                        </div>

                        {/* Generate Button */}
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

export default HTSIndexReport;