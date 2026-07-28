import React, { useEffect, useState } from "react";
import axios from "axios";
import FileSaver from "file-saver";
import { toast } from "react-toastify";

import { CardBody } from "reactstrap";
import { Card, Paper, Box, Typography, TextField } from "@material-ui/core";

import Autocomplete from "@material-ui/lab/Autocomplete";
import ToggleButton from "@material-ui/lab/ToggleButton";
import ToggleButtonGroup from "@material-ui/lab/ToggleButtonGroup";

import BusinessIcon from "@material-ui/icons/Business";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import PictureAsPdfIcon from "@material-ui/icons/PictureAsPdf";
import DescriptionIcon from "@material-ui/icons/Description";

import { Button, Message } from "semantic-ui-react";

import { token, url as baseUrl } from "../../../api";
import ProgressComponent from "./ProgressComponent";

const HTS_MSF = () => {
    const currentDate = new Date().toISOString().split("T")[0];

    const [loading, setLoading] = useState(false);
    const [facilities, setFacilities] = useState([]);
    const [months, setMonths] = useState([]);

    const [objValues, setObjValues] = useState({
        organisationUnitId: "",
        organisationUnitName: "",
        month: "",
        format: "excel",
    });

    useEffect(() => {
        loadFacilities();
        loadMonths();
    }, []);

    const loadFacilities = () => {
        axios
            .get(`${baseUrl}account`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
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

    const loadMonths = () => {
        axios
            .get(`${baseUrl}months`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((response) => {
                setMonths(response.data || []);
            })
            .catch(() => {
                toast.error("Unable to load months");
            });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        setLoading(true);

        axios
            .get(
                `${baseUrl}hts_msf?facilityId=${objValues.organisationUnitId}&month=${objValues.month}&format=${objValues.format}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    responseType: "blob",
                }
            )
            .then((response) => {
                setLoading(false);

                const extension =
                    objValues.format === "pdf" ? "pdf" : "xlsx";

                const mimeType =
                    objValues.format === "pdf"
                        ? "application/pdf"
                        : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

                const blob = new Blob([response.data], {
                    type: mimeType,
                });

                const fileName = `${objValues.organisationUnitName}_HTS_MSF_${objValues.month}.${extension}`;

                FileSaver.saveAs(blob, fileName);

                toast.success("Report generated successfully");
            })
            .catch((error) => {
                setLoading(false);

                if (error.response?.data?.apierror?.message) {
                    toast.error(error.response.data.apierror.message);
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
                            fontWeight: 600,
                            color: "#014D88",
                        }}
                    >
                        HTS Monthly Summary Form Report
                    </Typography>

                    <Typography
                        variant="body2"
                        color="textSecondary"
                        style={{ marginTop: 5 }}
                    >
                        Select your reporting month, facility and preferred
                        export format.
                    </Typography>
                </Box>

                <Paper
                    elevation={2}
                    style={{
                        padding: 24,
                        borderRadius: 12,
                        backgroundColor: "#fafafa",
                    }}
                >
                    <div className="row">
                        {/* REPORTING MONTH */}
                        <div className="col-md-6 mb-4">
                            <Typography
                                variant="subtitle2"
                                style={{
                                    marginBottom: 8,
                                    display: "flex",
                                    alignItems: "center",
                                    fontWeight: 600,
                                }}
                            >
                                <CalendarTodayIcon
                                    fontSize="small"
                                    style={{
                                        marginRight: 8,
                                        color: "#014D88",
                                    }}
                                />
                                Reporting Month
                            </Typography>

                            <Autocomplete
                                options={months}
                                value={objValues.month}
                                onChange={(event, value) =>
                                    setObjValues({
                                        ...objValues,
                                        month: value || "",
                                    })
                                }
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        variant="outlined"
                                        fullWidth
                                        placeholder="Search reporting month..."
                                        helperText="Select reporting period"
                                    />
                                )}
                            />
                        </div>

                        {/* FACILITY */}
                        <div className="col-md-6 mb-4">
                            <Typography
                                variant="subtitle2"
                                style={{
                                    marginBottom: 8,
                                    display: "flex",
                                    alignItems: "center",
                                    fontWeight: 600,
                                }}
                            >
                                <BusinessIcon
                                    fontSize="small"
                                    style={{
                                        marginRight: 8,
                                        color: "#014D88",
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
                                            value?.organisationUnitName || "",
                                    });
                                }}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        variant="outlined"
                                        fullWidth
                                        placeholder="Search facility..."
                                        helperText="Select facility"
                                    />
                                )}
                            />
                        </div>

                        {/* EXPORT FORMAT */}
                        <div className="col-md-12 mb-4">
                            <Typography
                                variant="subtitle2"
                                style={{
                                    marginBottom: 12,
                                    fontWeight: 600,
                                }}
                            >
                                Export Format
                            </Typography>

                            <ToggleButtonGroup
                                value={objValues.format}
                                exclusive
                                onChange={(event, value) => {
                                    if (value) {
                                        setObjValues({
                                            ...objValues,
                                            format: value,
                                        });
                                    }
                                }}
                            >
                                <ToggleButton
                                    value="excel"
                                    style={{
                                        padding: "12px 24px",
                                        textTransform: "none",
                                    }}
                                >
                                    <DescriptionIcon
                                        style={{
                                            marginRight: 8,
                                            color: "#217346",
                                        }}
                                    />
                                    Excel (.xlsx)
                                </ToggleButton>

                                <ToggleButton
                                    value="pdf"
                                    style={{
                                        padding: "12px 24px",
                                        textTransform: "none",
                                    }}
                                >
                                    <PictureAsPdfIcon
                                        style={{
                                            marginRight: 8,
                                            color: "#E53935",
                                        }}
                                    />
                                    PDF (.pdf)
                                </ToggleButton>
                            </ToggleButtonGroup>
                        </div>

                        {/* ACTION */}
                        <div className="col-md-12">
                            <Button
                                primary
                                icon="download"
                                content={
                                    loading
                                        ? "Generating..."
                                        : "Generate Report"
                                }
                                labelPosition="left"
                                style={{
                                    backgroundColor: "#014D88",
                                }}
                                onClick={handleSubmit}
                                disabled={
                                    !objValues.month ||
                                    !objValues.organisationUnitId ||
                                    !objValues.format ||
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
                            borderRadius: 10,
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

export default HTS_MSF;