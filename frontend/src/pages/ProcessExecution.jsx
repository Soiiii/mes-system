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
  Chip,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Grid,
  Card,
  CardContent,
  Alert,
} from '@mui/material';
import {
  PlayArrow as PlayArrowIcon,
  CheckCircle as CheckCircleIcon,
} from '@mui/icons-material';
import { lotsApi, processesApi, equipmentApi } from '../services/api';

function ProcessExecution() {
  const [lots, setLots] = useState([]);
  const [processes, setProcesses] = useState([]);
  const [equipments, setEquipments] = useState([]);
  const [openExecuteDialog, setOpenExecuteDialog] = useState(false);
  const [selectedLot, setSelectedLot] = useState(null);

  const [formData, setFormData] = useState({
    processId: '',
    equipmentId: '',
    inputQuantity: '',
    outputQuantity: '',
    defectQuantity: '',
    result: 'PASS',
    operator: '',
    remarks: '',
  });

  useEffect(() => {
    loadLots();
    loadProcesses();
    loadEquipments();
  }, []);

  const loadLots = async () => {
    try {
      const response = await lotsApi.getAll();
      // IN_PROGRESS 또는 CREATED 상태의 LOT만 표시
      const activeLots = response.data.filter(
        (lot) => lot.status === 'IN_PROGRESS' || lot.status === 'CREATED'
      );
      setLots(activeLots);
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

  const loadEquipments = async () => {
    try {
      const response = await equipmentApi.getAll();
      setEquipments(response.data);
    } catch (error) {
      console.error('Failed to load equipments:', error);
    }
  };

  const handleOpenExecute = (lot) => {
    setSelectedLot(lot);
    setFormData({
      processId: '',
      equipmentId: '',
      inputQuantity: lot.quantity.toString(),
      outputQuantity: '',
      defectQuantity: '0',
      result: 'PASS',
      operator: '',
      remarks: '',
    });
    setOpenExecuteDialog(true);
  };

  const handleExecuteProcess = async () => {
    try {
      if (!formData.processId || !formData.equipmentId || !formData.operator) {
        alert('Please fill in all required fields');
        return;
      }

      const outputQty = parseInt(formData.outputQuantity);
      const defectQty = parseInt(formData.defectQuantity);
      const inputQty = parseInt(formData.inputQuantity);

      if (outputQty + defectQty > inputQty) {
        alert('Output + Defect cannot exceed Input quantity');
        return;
      }

      await lotsApi.addHistory({
        lotId: selectedLot.id,
        processId: Number(formData.processId),
        equipmentId: Number(formData.equipmentId),
        inputQuantity: inputQty,
        outputQuantity: outputQty,
        defectQuantity: defectQty,
        result: formData.result,
        operator: formData.operator,
        remarks: formData.remarks,
      });

      // LOT 상태를 IN_PROGRESS로 업데이트
      if (selectedLot.status === 'CREATED') {
        await lotsApi.updateStatus(selectedLot.id, 'IN_PROGRESS');
      }

      loadLots();
      setOpenExecuteDialog(false);
      alert('Process executed successfully!');
    } catch (error) {
      console.error('Failed to execute process:', error);
      alert('Failed to execute process: ' + error.message);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'IN_PROGRESS':
        return 'primary';
      case 'CREATED':
        return 'warning';
      default:
        return 'default';
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Process Execution</Typography>
      </Box>

      <Alert severity="info" sx={{ mb: 3 }}>
        Select a LOT and execute processes. Track input, output, and defect quantities for each
        process step.
      </Alert>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>LOT Number</TableCell>
              <TableCell>Product</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Processes Completed</TableCell>
              <TableCell>Total Defects</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {lots.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography color="textSecondary" py={3}>
                    No active LOTs. Create a LOT from the LOT Tracking page.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              lots.map((lot) => (
                <TableRow key={lot.id}>
                  <TableCell>
                    <Typography variant="body2" fontWeight="bold">
                      {lot.lotNumber}
                    </Typography>
                  </TableCell>
                  <TableCell>{lot.productName}</TableCell>
                  <TableCell>{lot.quantity}</TableCell>
                  <TableCell>
                    <Chip label={lot.status} color={getStatusColor(lot.status)} size="small" />
                  </TableCell>
                  <TableCell>{lot.totalProcessed || 0}</TableCell>
                  <TableCell>
                    <Chip
                      label={lot.defectCount || 0}
                      color={lot.defectCount > 0 ? 'error' : 'success'}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Button
                      variant="contained"
                      size="small"
                      startIcon={<PlayArrowIcon />}
                      onClick={() => handleOpenExecute(lot)}
                    >
                      Execute Process
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Execute Process Dialog */}
      <Dialog
        open={openExecuteDialog}
        onClose={() => setOpenExecuteDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Execute Process
          <Typography variant="subtitle2" color="textSecondary">
            LOT: {selectedLot?.lotNumber} | Product: {selectedLot?.productName}
          </Typography>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <Alert severity="warning" sx={{ mb: 2 }}>
                  Record the actual production results for this process step.
                </Alert>
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth required>
                  <InputLabel>Process</InputLabel>
                  <Select
                    value={formData.processId}
                    onChange={(e) => setFormData({ ...formData, processId: e.target.value })}
                    label="Process"
                  >
                    {processes.map((process) => (
                      <MenuItem key={process.id} value={process.id}>
                        {process.name} ({process.code})
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth required>
                  <InputLabel>Equipment</InputLabel>
                  <Select
                    value={formData.equipmentId}
                    onChange={(e) => setFormData({ ...formData, equipmentId: e.target.value })}
                    label="Equipment"
                  >
                    {equipments.map((equipment) => (
                      <MenuItem key={equipment.id} value={equipment.id}>
                        {equipment.name} - {equipment.location}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12}>
                <Card variant="outlined">
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Quantity Information
                    </Typography>
                    <Grid container spacing={2}>
                      <Grid item xs={4}>
                        <TextField
                          label="Input Quantity"
                          type="number"
                          fullWidth
                          required
                          value={formData.inputQuantity}
                          onChange={(e) =>
                            setFormData({ ...formData, inputQuantity: e.target.value })
                          }
                          helperText="Units entering this process"
                        />
                      </Grid>
                      <Grid item xs={4}>
                        <TextField
                          label="Output Quantity"
                          type="number"
                          fullWidth
                          required
                          value={formData.outputQuantity}
                          onChange={(e) =>
                            setFormData({ ...formData, outputQuantity: e.target.value })
                          }
                          helperText="Good units produced"
                        />
                      </Grid>
                      <Grid item xs={4}>
                        <TextField
                          label="Defect Quantity"
                          type="number"
                          fullWidth
                          value={formData.defectQuantity}
                          onChange={(e) =>
                            setFormData({ ...formData, defectQuantity: e.target.value })
                          }
                          helperText="Defective units"
                        />
                      </Grid>
                    </Grid>

                    {formData.inputQuantity &&
                      formData.outputQuantity &&
                      formData.defectQuantity && (
                        <Box mt={2}>
                          <Typography variant="body2" color="textSecondary">
                            Summary: {formData.inputQuantity} input → {formData.outputQuantity}{' '}
                            output + {formData.defectQuantity} defects ={' '}
                            {parseInt(formData.outputQuantity) + parseInt(formData.defectQuantity)}
                          </Typography>
                          {parseInt(formData.outputQuantity) + parseInt(formData.defectQuantity) >
                            parseInt(formData.inputQuantity) && (
                            <Typography variant="body2" color="error" mt={1}>
                              ⚠️ Output + Defects exceeds Input quantity!
                            </Typography>
                          )}
                        </Box>
                      )}
                  </CardContent>
                </Card>
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth>
                  <InputLabel>Result</InputLabel>
                  <Select
                    value={formData.result}
                    onChange={(e) => setFormData({ ...formData, result: e.target.value })}
                    label="Result"
                  >
                    <MenuItem value="PASS">
                      <Box display="flex" alignItems="center" gap={1}>
                        <CheckCircleIcon color="success" fontSize="small" />
                        PASS
                      </Box>
                    </MenuItem>
                    <MenuItem value="FAIL">
                      <Box display="flex" alignItems="center" gap={1}>
                        <CheckCircleIcon color="error" fontSize="small" />
                        FAIL
                      </Box>
                    </MenuItem>
                    <MenuItem value="REWORK">
                      <Box display="flex" alignItems="center" gap={1}>
                        <CheckCircleIcon color="warning" fontSize="small" />
                        REWORK
                      </Box>
                    </MenuItem>
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={6}>
                <TextField
                  label="Operator Name"
                  fullWidth
                  required
                  value={formData.operator}
                  onChange={(e) => setFormData({ ...formData, operator: e.target.value })}
                  helperText="Person executing this process"
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  label="Remarks"
                  fullWidth
                  multiline
                  rows={2}
                  value={formData.remarks}
                  onChange={(e) => setFormData({ ...formData, remarks: e.target.value })}
                  placeholder="Any notes about this process execution..."
                />
              </Grid>
            </Grid>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenExecuteDialog(false)}>Cancel</Button>
          <Button onClick={handleExecuteProcess} variant="contained" color="primary">
            Execute Process
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default ProcessExecution;
