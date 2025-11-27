import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Chip,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  Card,
  CardContent,
  Tabs,
  Tab,
  Alert,
} from '@mui/material';
import {
  Add as AddIcon,
  Visibility as VisibilityIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  Warning as WarningIcon,
  Edit as EditIcon,
} from '@mui/icons-material';
import { qualityInspectionApi, lotsApi, processesApi } from '../services/api';

function QualityInspection() {
  const [activeTab, setActiveTab] = useState(0);
  const [inspections, setInspections] = useState([]);
  const [standards, setStandards] = useState([]);
  const [lots, setLots] = useState([]);
  const [processes, setProcesses] = useState([]);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [openDetailDialog, setOpenDetailDialog] = useState(false);
  const [openStandardDialog, setOpenStandardDialog] = useState(false);
  const [openMeasureDialog, setOpenMeasureDialog] = useState(false);
  const [selectedInspection, setSelectedInspection] = useState(null);
  const [measurementData, setMeasurementData] = useState([]);

  const [formData, setFormData] = useState({
    lotId: '',
    processId: '',
    type: 'IN_PROCESS',
    sampleSize: '',
    inspector: '',
    items: [],
    remarks: '',
  });

  const [standardFormData, setStandardFormData] = useState({
    code: '',
    name: '',
    category: '',
    standardValue: '',
    upperLimit: '',
    lowerLimit: '',
    unit: '',
    applicableType: 'IN_PROCESS',
    description: '',
  });

  useEffect(() => {
    loadInspections();
    loadStandards();
    loadLots();
    loadProcesses();
  }, []);

  const loadInspections = async () => {
    try {
      const response = await qualityInspectionApi.getAll();
      setInspections(response.data);
    } catch (error) {
      console.error('Failed to load inspections:', error);
    }
  };

  const loadStandards = async () => {
    try {
      const response = await qualityInspectionApi.getAllStandards();
      setStandards(response.data);
    } catch (error) {
      console.error('Failed to load standards:', error);
    }
  };

  const loadLots = async () => {
    try {
      const response = await lotsApi.getAll();
      setLots(response.data);
    } catch (error) {
      console.error('Failed to load lots:', error);
    }
  };

  const loadProcesses = async () => {
    try {
      const response = await processesApi.getAll();
      setProcesses(response.data);
    } catch (error) {
      console.error('Failed to load processes:', error);
    }
  };

  const handleCreateInspection = async () => {
    try {
      // 기본 검사 항목 생성
      const applicableStandards = standards.filter(s => s.applicableType === formData.type);
      
      if (applicableStandards.length === 0) {
        alert('No inspection standards available for this type. Please create standards first.');
        return;
      }

      const items = applicableStandards.map(standard => ({
        standardId: standard.id,
        measuredValue: '',
        result: 'PENDING',
        remarks: '',
      }));

      await qualityInspectionApi.create({
        ...formData,
        lotId: Number(formData.lotId),
        processId: formData.processId ? Number(formData.processId) : null,
        sampleSize: Number(formData.sampleSize),
        items: items,
      });

      loadInspections();
      setOpenCreateDialog(false);
      setFormData({
        lotId: '',
        processId: '',
        type: 'IN_PROCESS',
        sampleSize: '',
        inspector: '',
        items: [],
        remarks: '',
      });
    } catch (error) {
      console.error('Failed to create inspection:', error);
      alert('Failed to create inspection: ' + error.message);
    }
  };

  const handleCreateStandard = async () => {
    try {
      await qualityInspectionApi.createStandard(standardFormData);
      loadStandards();
      setOpenStandardDialog(false);
      setStandardFormData({
        code: '',
        name: '',
        category: '',
        standardValue: '',
        upperLimit: '',
        lowerLimit: '',
        unit: '',
        applicableType: 'IN_PROCESS',
        description: '',
      });
    } catch (error) {
      console.error('Failed to create standard:', error);
      alert('Failed to create standard: ' + error.message);
    }
  };

  const handleViewDetail = (inspection) => {
    setSelectedInspection(inspection);
    setOpenDetailDialog(true);
  };

  const handleOpenMeasure = (inspection) => {
    setSelectedInspection(inspection);
    
    // 측정 데이터 초기화
    const initialData = inspection.items.map(item => ({
      id: item.id,
      standardId: item.standardId,
      standardName: item.standardName,
      standardValue: item.standardValue,
      lowerLimit: item.lowerLimit,
      upperLimit: item.upperLimit,
      unit: item.unit,
      measuredValue: item.measuredValue || '',
      result: item.result || 'PENDING',
      remarks: item.remarks || '',
    }));
    
    setMeasurementData(initialData);
    setOpenMeasureDialog(true);
  };

  const handleMeasurementChange = (index, field, value) => {
    const newData = [...measurementData];
    newData[index][field] = value;

    // 자동 판정: 측정값이 입력되면 범위 체크
    if (field === 'measuredValue' && value) {
      const item = newData[index];
      const measured = parseFloat(value);
      const lower = parseFloat(item.lowerLimit);
      const upper = parseFloat(item.upperLimit);

      if (!isNaN(measured) && !isNaN(lower) && !isNaN(upper)) {
        if (measured >= lower && measured <= upper) {
          newData[index].result = 'PASS';
        } else {
          newData[index].result = 'FAIL';
        }
      }
    }

    setMeasurementData(newData);
  };

  const handleSaveMeasurements = async () => {
    try {
      // 여기서는 간단히 전체 검사를 재생성하는 방식 사용
      // 실제로는 PATCH API를 만들어야 하지만, 빠른 구현을 위해 재생성
      
      const items = measurementData.map(item => ({
        standardId: item.standardId,
        measuredValue: item.measuredValue,
        result: item.result,
        remarks: item.remarks,
      }));

      // 검사 재생성
      await qualityInspectionApi.create({
        lotId: selectedInspection.lotId,
        processId: selectedInspection.processId,
        type: selectedInspection.type,
        sampleSize: selectedInspection.sampleSize,
        inspector: selectedInspection.inspector,
        items: items,
        remarks: selectedInspection.remarks,
      });

      loadInspections();
      setOpenMeasureDialog(false);
      alert('Measurements saved successfully!');
    } catch (error) {
      console.error('Failed to save measurements:', error);
      alert('Failed to save measurements: ' + error.message);
    }
  };

  const handleCompleteInspection = async (id, result) => {
    try {
      await qualityInspectionApi.complete(id, result);
      loadInspections();
      setOpenDetailDialog(false);
      setOpenMeasureDialog(false);
    } catch (error) {
      console.error('Failed to complete inspection:', error);
      alert('Failed to complete inspection: ' + error.message);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'IN_PROGRESS':
        return 'primary';
      case 'PENDING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const getResultColor = (result) => {
    switch (result) {
      case 'PASS':
        return 'success';
      case 'FAIL':
        return 'error';
      case 'CONDITIONAL_PASS':
        return 'warning';
      default:
        return 'default';
    }
  };

  const getResultIcon = (result) => {
    switch (result) {
      case 'PASS':
        return <CheckCircleIcon color="success" />;
      case 'FAIL':
        return <CancelIcon color="error" />;
      case 'CONDITIONAL_PASS':
        return <WarningIcon color="warning" />;
      default:
        return null;
    }
  };

  const calculateOverallResult = () => {
    if (measurementData.length === 0) return null;
    
    const hasAnyFail = measurementData.some(item => item.result === 'FAIL');
    const allPass = measurementData.every(item => item.result === 'PASS');
    
    if (hasAnyFail) return 'FAIL';
    if (allPass) return 'PASS';
    return 'CONDITIONAL_PASS';
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Quality Inspection</Typography>
        <Box display="flex" gap={2}>
          <Button
            variant="outlined"
            startIcon={<AddIcon />}
            onClick={() => setOpenStandardDialog(true)}
          >
            New Standard
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setOpenCreateDialog(true)}
          >
            New Inspection
          </Button>
        </Box>
      </Box>

      <Tabs value={activeTab} onChange={(e, v) => setActiveTab(v)} sx={{ mb: 2 }}>
        <Tab label="Inspections" />
        <Tab label="Standards" />
      </Tabs>

      {/* Inspections Tab */}
      {activeTab === 0 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Inspection #</TableCell>
                <TableCell>LOT Number</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Result</TableCell>
                <TableCell>Inspector</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {inspections.map((inspection) => (
                <TableRow key={inspection.id}>
                  <TableCell>
                    <Typography variant="body2" fontWeight="bold">
                      {inspection.inspectionNumber}
                    </Typography>
                  </TableCell>
                  <TableCell>{inspection.lotNumber}</TableCell>
                  <TableCell>
                    <Chip label={inspection.type} size="small" />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={inspection.status}
                      color={getStatusColor(inspection.status)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {inspection.result ? (
                      <Chip
                        icon={getResultIcon(inspection.result)}
                        label={inspection.result}
                        color={getResultColor(inspection.result)}
                        size="small"
                      />
                    ) : (
                      '-'
                    )}
                  </TableCell>
                  <TableCell>{inspection.inspector || '-'}</TableCell>
                  <TableCell>
                    {inspection.inspectionDate
                      ? new Date(inspection.inspectionDate).toLocaleString()
                      : '-'}
                  </TableCell>
                  <TableCell>
                    <IconButton
                      size="small"
                      onClick={() => handleOpenMeasure(inspection)}
                      title="Measure & Inspect"
                      color="primary"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleViewDetail(inspection)}
                      title="View Details"
                    >
                      <VisibilityIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Standards Tab */}
      {activeTab === 1 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Code</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Category</TableCell>
                <TableCell>Standard Value</TableCell>
                <TableCell>Range</TableCell>
                <TableCell>Unit</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {standards.map((standard) => (
                <TableRow key={standard.id}>
                  <TableCell>{standard.code}</TableCell>
                  <TableCell>{standard.name}</TableCell>
                  <TableCell>{standard.category}</TableCell>
                  <TableCell>{standard.standardValue}</TableCell>
                  <TableCell>
                    {standard.lowerLimit} ~ {standard.upperLimit}
                  </TableCell>
                  <TableCell>{standard.unit}</TableCell>
                  <TableCell>
                    <Chip label={standard.applicableType} size="small" />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={standard.isActive ? 'Active' : 'Inactive'}
                      color={standard.isActive ? 'success' : 'default'}
                      size="small"
                    />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Measurement Dialog */}
      <Dialog
        open={openMeasureDialog}
        onClose={() => setOpenMeasureDialog(false)}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle>
          Quality Inspection - Measure & Inspect
          <Typography variant="subtitle2" color="textSecondary">
            {selectedInspection?.inspectionNumber} | LOT: {selectedInspection?.lotNumber}
          </Typography>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Alert severity="info" sx={{ mb: 2 }}>
              Enter measured values for each inspection item. The system will automatically
              determine PASS/FAIL based on the acceptable range.
            </Alert>

            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Inspection Item</TableCell>
                    <TableCell>Standard Value</TableCell>
                    <TableCell>Acceptable Range</TableCell>
                    <TableCell width="150">Measured Value</TableCell>
                    <TableCell>Result</TableCell>
                    <TableCell width="200">Remarks</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {measurementData.map((item, index) => (
                    <TableRow key={item.id}>
                      <TableCell>
                        <Typography variant="body2" fontWeight="bold">
                          {item.standardName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        {item.standardValue} {item.unit}
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2" color="textSecondary">
                          {item.lowerLimit} ~ {item.upperLimit} {item.unit}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <TextField
                          size="small"
                          type="number"
                          value={item.measuredValue}
                          onChange={(e) =>
                            handleMeasurementChange(index, 'measuredValue', e.target.value)
                          }
                          placeholder="Enter value"
                          fullWidth
                        />
                      </TableCell>
                      <TableCell>
                        <FormControl size="small" fullWidth>
                          <Select
                            value={item.result}
                            onChange={(e) =>
                              handleMeasurementChange(index, 'result', e.target.value)
                            }
                          >
                            <MenuItem value="PASS">
                              <Box display="flex" alignItems="center" gap={1}>
                                <CheckCircleIcon color="success" fontSize="small" />
                                PASS
                              </Box>
                            </MenuItem>
                            <MenuItem value="FAIL">
                              <Box display="flex" alignItems="center" gap={1}>
                                <CancelIcon color="error" fontSize="small" />
                                FAIL
                              </Box>
                            </MenuItem>
                          </Select>
                        </FormControl>
                      </TableCell>
                      <TableCell>
                        <TextField
                          size="small"
                          value={item.remarks}
                          onChange={(e) =>
                            handleMeasurementChange(index, 'remarks', e.target.value)
                          }
                          placeholder="Notes"
                          fullWidth
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            {measurementData.length > 0 && (
              <Box mt={3}>
                <Card>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Overall Result
                    </Typography>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Chip
                        icon={getResultIcon(calculateOverallResult())}
                        label={calculateOverallResult() || 'PENDING'}
                        color={getResultColor(calculateOverallResult())}
                        size="large"
                      />
                      <Typography variant="body2" color="textSecondary">
                        Passed: {measurementData.filter(i => i.result === 'PASS').length} / 
                        Failed: {measurementData.filter(i => i.result === 'FAIL').length}
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenMeasureDialog(false)}>Cancel</Button>
          <Button onClick={handleSaveMeasurements} variant="outlined">
            Save Measurements
          </Button>
          {selectedInspection?.status === 'PENDING' && calculateOverallResult() && (
            <Button
              onClick={() =>
                handleCompleteInspection(selectedInspection.id, calculateOverallResult())
              }
              variant="contained"
              color={calculateOverallResult() === 'PASS' ? 'success' : 'error'}
            >
              Complete Inspection as {calculateOverallResult()}
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* Create Inspection Dialog */}
      <Dialog
        open={openCreateDialog}
        onClose={() => setOpenCreateDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Create New Inspection</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <FormControl fullWidth>
              <InputLabel>LOT</InputLabel>
              <Select
                value={formData.lotId}
                onChange={(e) => setFormData({ ...formData, lotId: e.target.value })}
                label="LOT"
              >
                {lots.map((lot) => (
                  <MenuItem key={lot.id} value={lot.id}>
                    {lot.lotNumber} - {lot.productName}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Process (Optional)</InputLabel>
              <Select
                value={formData.processId}
                onChange={(e) => setFormData({ ...formData, processId: e.target.value })}
                label="Process (Optional)"
              >
                <MenuItem value="">None</MenuItem>
                {processes.map((process) => (
                  <MenuItem key={process.id} value={process.id}>
                    {process.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Inspection Type</InputLabel>
              <Select
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                label="Inspection Type"
              >
                <MenuItem value="INCOMING">Incoming (수입 검사)</MenuItem>
                <MenuItem value="IN_PROCESS">In-Process (공정 검사)</MenuItem>
                <MenuItem value="FINAL">Final (최종 검사)</MenuItem>
                <MenuItem value="OUTGOING">Outgoing (출하 검사)</MenuItem>
              </Select>
            </FormControl>

            <TextField
              label="Sample Size"
              type="number"
              fullWidth
              value={formData.sampleSize}
              onChange={(e) => setFormData({ ...formData, sampleSize: e.target.value })}
            />

            <TextField
              label="Inspector Name"
              fullWidth
              value={formData.inspector}
              onChange={(e) => setFormData({ ...formData, inspector: e.target.value })}
            />

            <TextField
              label="Remarks"
              fullWidth
              multiline
              rows={2}
              value={formData.remarks}
              onChange={(e) => setFormData({ ...formData, remarks: e.target.value })}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCreateDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateInspection} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>

      {/* Inspection Detail Dialog */}
      <Dialog
        open={openDetailDialog}
        onClose={() => setOpenDetailDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Inspection Details: {selectedInspection?.inspectionNumber}</DialogTitle>
        <DialogContent>
          {selectedInspection && (
            <Box sx={{ pt: 2 }}>
              <Grid container spacing={2} mb={3}>
                <Grid item xs={6}>
                  <Card>
                    <CardContent>
                      <Typography color="textSecondary" gutterBottom>
                        LOT Number
                      </Typography>
                      <Typography variant="h6">{selectedInspection.lotNumber}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={3}>
                  <Card>
                    <CardContent>
                      <Typography color="textSecondary" gutterBottom>
                        Type
                      </Typography>
                      <Chip label={selectedInspection.type} />
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={3}>
                  <Card>
                    <CardContent>
                      <Typography color="textSecondary" gutterBottom>
                        Status
                      </Typography>
                      <Chip
                        label={selectedInspection.status}
                        color={getStatusColor(selectedInspection.status)}
                      />
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <Typography variant="h6" gutterBottom>
                Inspection Items
              </Typography>

              {selectedInspection.items && selectedInspection.items.length > 0 ? (
                <TableContainer component={Paper} sx={{ mb: 2 }}>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Item</TableCell>
                        <TableCell>Measured</TableCell>
                        <TableCell>Standard</TableCell>
                        <TableCell>Range</TableCell>
                        <TableCell>Result</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedInspection.items.map((item) => (
                        <TableRow key={item.id}>
                          <TableCell>{item.standardName}</TableCell>
                          <TableCell>
                            {item.measuredValue ? `${item.measuredValue} ${item.unit}` : '-'}
                          </TableCell>
                          <TableCell>
                            {item.standardValue} {item.unit}
                          </TableCell>
                          <TableCell>
                            {item.lowerLimit} ~ {item.upperLimit}
                          </TableCell>
                          <TableCell>
                            <Chip
                              icon={getResultIcon(item.result)}
                              label={item.result}
                              color={getResultColor(item.result)}
                              size="small"
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <Typography color="textSecondary" align="center" py={2}>
                  No inspection items
                </Typography>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDetailDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* Create Standard Dialog */}
      <Dialog
        open={openStandardDialog}
        onClose={() => setOpenStandardDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Create Inspection Standard</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Code"
              fullWidth
              value={standardFormData.code}
              onChange={(e) =>
                setStandardFormData({ ...standardFormData, code: e.target.value })
              }
            />

            <TextField
              label="Name"
              fullWidth
              value={standardFormData.name}
              onChange={(e) =>
                setStandardFormData({ ...standardFormData, name: e.target.value })
              }
            />

            <TextField
              label="Category"
              fullWidth
              placeholder="e.g., Dimension, Appearance, Performance"
              value={standardFormData.category}
              onChange={(e) =>
                setStandardFormData({ ...standardFormData, category: e.target.value })
              }
            />

            <Grid container spacing={2}>
              <Grid item xs={4}>
                <TextField
                  label="Standard Value"
                  fullWidth
                  value={standardFormData.standardValue}
                  onChange={(e) =>
                    setStandardFormData({ ...standardFormData, standardValue: e.target.value })
                  }
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  label="Lower Limit"
                  fullWidth
                  value={standardFormData.lowerLimit}
                  onChange={(e) =>
                    setStandardFormData({ ...standardFormData, lowerLimit: e.target.value })
                  }
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  label="Upper Limit"
                  fullWidth
                  value={standardFormData.upperLimit}
                  onChange={(e) =>
                    setStandardFormData({ ...standardFormData, upperLimit: e.target.value })
                  }
                />
              </Grid>
            </Grid>

            <TextField
              label="Unit"
              fullWidth
              placeholder="e.g., mm, kg, °C"
              value={standardFormData.unit}
              onChange={(e) =>
                setStandardFormData({ ...standardFormData, unit: e.target.value })
              }
            />

            <FormControl fullWidth>
              <InputLabel>Applicable Type</InputLabel>
              <Select
                value={standardFormData.applicableType}
                onChange={(e) =>
                  setStandardFormData({ ...standardFormData, applicableType: e.target.value })
                }
                label="Applicable Type"
              >
                <MenuItem value="INCOMING">Incoming</MenuItem>
                <MenuItem value="IN_PROCESS">In-Process</MenuItem>
                <MenuItem value="FINAL">Final</MenuItem>
                <MenuItem value="OUTGOING">Outgoing</MenuItem>
              </Select>
            </FormControl>

            <TextField
              label="Description"
              fullWidth
              multiline
              rows={2}
              value={standardFormData.description}
              onChange={(e) =>
                setStandardFormData({ ...standardFormData, description: e.target.value })
              }
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenStandardDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateStandard} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default QualityInspection;
