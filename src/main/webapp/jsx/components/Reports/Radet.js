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
    TextField,
    Switch,
    FormControlLabel
} from "@material-ui/core";

import Autocomplete from "@material-ui/lab/Autocomplete";

import AssessmentIcon from "@material-ui/icons/Assessment";
import BusinessIcon from "@material-ui/icons/Business";
import DateRangeIcon from "@material-ui/icons/DateRange";

import { Button, Message } from "semantic-ui-react";

import { token, url as baseUrl } from "../../../api";
import ProgressComponent from "./ProgressComponent";

const PatientLineList = () => {
    const currentDate = new Date().toISOString().split("T")[0];

    const [loading, setLoading] = useState(false);
    const [facilities, setFacilities] = useState([]);
    const [asAtToday, setAsAtToday] = useState(false);

    const [objValues, setObjValues] = useState({
        organisationUnitId: "",
        organisationUnitName: "",
        startDate: "",
        endDate: ""
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

    const handleDateChange = (event) => {
        setObjValues({
            ...objValues,
            [event.target.name]: event.target.value
        });
    };

    const handleAsAtToday = (event) => {
        const checked = event.target.checked;

        setAsAtToday(checked);

        if (checked) {
            setObjValues((prev) => ({
                ...prev,
                startDate: "1980-01-01",
                endDate: currentDate
            }));
        } else {
            setObjValues((prev) => ({
                ...prev,
                startDate: "",
                endDate: ""
            }));
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        setLoading(true);

        axios
            .get(
                `${baseUrl}reporting/radet?facilityId=${objValues.organisationUnitId}&startDate=${objValues.startDate}&endDate=${objValues.endDate}`,
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

                const fileName = `${objValues.organisationUnitName}_RADET_${objValues.startDate}_${objValues.endDate}.xlsx`;

                FileSaver.saveAs(blob, fileName);

                toast.success(
                    "RADET Report generated successfully"
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
                            fontWeight: 600,
                            color: "#014D88",
                            display: "flex",
                            alignItems: "center"
                        }}
                    >
                        <AssessmentIcon
                            style={{
                                marginRight: 10,
                                color: "#014D88"
                            }}
                        />
                        RADET Report
                    </Typography>

                    <Typography
                        variant="body2"
                        color="textSecondary"
                        style={{ marginTop: 5 }}
                    >
                        Generate RADET reports using a selected
                        reporting period and facility.
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
                        {/* DATE RANGE */}
                        <div className="col-md-12 mb-4">
                            <div className="row">
                                <div className="col-md-6">
                                    <Typography
                                        variant="subtitle2"
                                        style={{
                                            marginBottom: 8,
                                            display: "flex",
                                            alignItems: "center",
                                            fontWeight: 600
                                        }}
                                    >
                                        <DateRangeIcon
                                            fontSize="small"
                                            style={{
                                                marginRight: 8,
                                                color: "#014D88"
                                            }}
                                        />
                                        From Date
                                    </Typography>

                                    <TextField
                                        type="date"
                                        fullWidth
                                        variant="outlined"
                                        name="startDate"
                                        value={objValues.startDate}
                                        onChange={handleDateChange}
                                        onKeyDown={(e) => e.preventDefault()}
                                        onPaste={(e) => e.preventDefault()}
                                        InputLabelProps={{
                                            shrink: true
                                        }}
                                        inputProps={{
                                            min: "1980-01-01",
                                            max: currentDate
                                        }}
                                    />
                                </div>

                                <div className="col-md-6">
                                    <Typography
                                        variant="subtitle2"
                                        style={{
                                            marginBottom: 8,
                                            display: "flex",
                                            alignItems: "center",
                                            fontWeight: 600
                                        }}
                                    >
                                        <DateRangeIcon
                                            fontSize="small"
                                            style={{
                                                marginRight: 8,
                                                color: "#014D88"
                                            }}
                                        />
                                        To Date
                                    </Typography>

                                    <TextField
                                        type="date"
                                        fullWidth
                                        variant="outlined"
                                        name="endDate"
                                        value={objValues.endDate}
                                        onChange={handleDateChange}
                                        onKeyDown={(e) => e.preventDefault()}
                                        onPaste={(e) => e.preventDefault()}
                                        InputLabelProps={{
                                            shrink: true
                                        }}
                                        inputProps={{
                                            min: "1980-01-01",
                                            max: currentDate
                                        }}
                                    />
                                </div>
                            </div>
                        </div>

                        {/* AS AT TODAY */}
                        <div className="col-md-12 mb-4">
                            <Paper
                                variant="outlined"
                                style={{
                                    padding: 14,
                                    borderRadius: 10
                                }}
                            >
                                <FormControlLabel
                                    control={
                                        <Switch
                                            checked={asAtToday}
                                            onChange={
                                                handleAsAtToday
                                            }
                                            color="primary"
                                        />
                                    }
                                    label="Generate Report As At Today"
                                />
                            </Paper>
                        </div>

                        {/* FACILITY */}
                        <div className="col-md-12 mb-4">
                            <Typography
                                variant="subtitle2"
                                style={{
                                    marginBottom: 8,
                                    display: "flex",
                                    alignItems: "center",
                                    fontWeight: 600
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
                                onChange={(event, value) =>
                                    setObjValues({
                                        ...objValues,
                                        organisationUnitId:
                                            value?.organisationUnitId || "",
                                        organisationUnitName:
                                            value?.organisationUnitName || ""
                                    })
                                }
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        variant="outlined"
                                        fullWidth
                                        placeholder="Search facility..."
                                        helperText="Select a facility for the report"
                                    />
                                )}
                            />
                        </div>

                        {/* ACTION */}
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
                                    !objValues.startDate ||
                                    !objValues.endDate ||
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

export default PatientLineList;