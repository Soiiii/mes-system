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
} from '@mui/material';
import {
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineOppositeContent,
} from '@mui/lab';
import {
  Add as AddIcon,
  Visibility as VisibilityIcon,
  Search as SearchIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  HourglassEmpty as HourglassEmptyIcon,
} from '@mui/icons-material';
import { lotsApi, productsApi, workOrdersApi } from '../services/api';

function LotTracking() {
  const [lots, setLots] = useState([]);
  const [products, setProducts] = useState([]);
  const [workOrders, setWorkOrders] = useState([]);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [openHistoryDialog, setOpenHistoryDialog] = useState(false);
  const [selectedLot, setSelectedLot] = useState(null);
  const [lotHistory, setLotHistory] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  
  const [formData, setFormData] = useState({
    productId: '',
    workOrderId: '',
    quantity: '',
    remarks: '',
  });

  useEffect(() => {
    loadLots();
    loadProducts();
    loadWorkOrders();
  }, []);

  const loadLots = async () => {
    try {
      const response = await lotsApi.getAll();
      setLots(response.data);
    } catch (error) {
      console.error('Failed to load lots:', error);
    }
  };

  const loadProducts = async () => {
    try {
      const response = await productsApi.getAll();
      setProducts(response.data);
    } catch (error) {
      console.error('Failed to load products:', error);
    }
  };

  const loadWorkOrders = async () => {
    try {
      const response = await workOrdersApi.getAll();
      setWorkOrders(response.data);
    } catch (error) {
      console.error('Failed to load work orders:', error);
    }
  };

  const handleCreateLot = async () => {
    try {
      await lotsApi.create({
        ...formData,
        productId: Number(formData.productId),
        workOrderId: Number(formData.workOrderId),
        quantity: Number(formData.quantity),
      });
      loadLots();
      setOpenCreateDialog(false);
      setFormData({
        productId: '',
        workOrderId: '',
        quantity: '',
        remarks: '',
      });
    } catch (error) {
      console.error('Failed to create lot:', error);
      alert('Failed to create lot: ' + error.message);
    }
  };

  const handleViewHistory = async (lot) => {
    try {
      const response = await lotsApi.getHistory(lot.id);
      setLotHistory(response.data);
      setSelectedLot(lot);
      setOpenHistoryDialog(true);
    } catch (error) {
      console.error('Failed to load lot history:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      loadLots();
      return;
    }
    try {
      const response = await lotsApi.search(searchKeyword);
      setLots(response.data);
    } catch (error) {
      console.error('Failed to search lots:', error);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'IN_PROGRESS':
        return 'primary';
      case 'REJECTED':
        return 'error';
      case 'ON_HOLD':
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
      case 'REWORK':
        return <HourglassEmptyIcon color="warning" />;
      default:
        return <HourglassEmptyIcon />;
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">LOT Tracking</Typography>
        <Box display="flex" gap={2}>
          <TextField
            size="small"
            placeholder="Search LOT..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <Button
            variant="outlined"
            startIcon={<SearchIcon />}
            onClick={handleSearch}
          >
            Search
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setOpenCreateDialog(true)}
          >
            New LOT
          </Button>
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>LOT Number</TableCell>
              <TableCell>Product</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Created</TableCell>
              <TableCell>Processes</TableCell>
              <TableCell>Defects</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {lots.map((lot) => (
              <TableRow key={lot.id}>
                <TableCell>
                  <Typography variant="body2" fontWeight="bold">
                    {lot.lotNumber}
                  </Typography>
                </TableCell>
                <TableCell>{lot.productName}</TableCell>
                <TableCell>{lot.quantity}</TableCell>
                <TableCell>
                  <Chip
                    label={lot.status}
                    color={getStatusColor(lot.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {new Date(lot.createdAt).toLocaleString()}
                </TableCell>
                <TableCell>{lot.totalProcessed || 0}</TableCell>
                <TableCell>
                  <Chip
                    label={lot.defectCount || 0}
                    color={lot.defectCount > 0 ? 'error' : 'default'}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <IconButton
                    size="small"
                    onClick={() => handleViewHistory(lot)}
                    title="View History"
                  >
                    <VisibilityIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Create LOT Dialog */}
      <Dialog
        open={openCreateDialog}
        onClose={() => setOpenCreateDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Create New LOT</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <FormControl fullWidth>
              <InputLabel>Product</InputLabel>
              <Select
                value={formData.productId}
                onChange={(e) =>
                  setFormData({ ...formData, productId: e.target.value })
                }
                label="Product"
              >
                {products.map((product) => (
                  <MenuItem key={product.id} value={product.id}>
                    {product.name} ({product.code})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Work Order</InputLabel>
              <Select
                value={formData.workOrderId}
                onChange={(e) =>
                  setFormData({ ...formData, workOrderId: e.target.value })
                }
                label="Work Order"
              >
                {workOrders.map((wo) => (
                  <MenuItem key={wo.id} value={wo.id}>
                    WO #{wo.id} - {wo.status}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              label="Quantity"
              type="number"
              fullWidth
              value={formData.quantity}
              onChange={(e) =>
                setFormData({ ...formData, quantity: e.target.value })
              }
            />

            <TextField
              label="Remarks"
              fullWidth
              multiline
              rows={2}
              value={formData.remarks}
              onChange={(e) =>
                setFormData({ ...formData, remarks: e.target.value })
              }
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCreateDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateLot} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>

      {/* LOT History Dialog */}
      <Dialog
        open={openHistoryDialog}
        onClose={() => setOpenHistoryDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          LOT History: {selectedLot?.lotNumber}
        </DialogTitle>
        <DialogContent>
          {selectedLot && (
            <Box sx={{ pt: 2 }}>
              <Grid container spacing={2} mb={3}>
                <Grid item xs={6}>
                  <Card>
                    <CardContent>
                      <Typography color="textSecondary" gutterBottom>
                        Product
                      </Typography>
                      <Typography variant="h6">
                        {selectedLot.productName}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={3}>
                  <Card>
                    <CardContent>
                      <Typography color="textSecondary" gutterBottom>
                        Quantity
                      </Typography>
                      <Typography variant="h6">
                        {selectedLot.quantity}
                      </Typography>
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
                        label={selectedLot.status}
                        color={getStatusColor(selectedLot.status)}
                      />
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <Typography variant="h6" gutterBottom>
                Process History
              </Typography>

              {lotHistory.length === 0 ? (
                <Typography color="textSecondary" align="center" py={4}>
                  No process history yet
                </Typography>
              ) : (
                <Timeline position="alternate">
                  {lotHistory.map((history, index) => (
                    <TimelineItem key={history.id}>
                      <TimelineOppositeContent color="textSecondary">
                        {new Date(history.processedAt).toLocaleString()}
                      </TimelineOppositeContent>
                      <TimelineSeparator>
                        <TimelineDot color={history.result === 'PASS' ? 'success' : 'error'}>
                          {getResultIcon(history.result)}
                        </TimelineDot>
                        {index < lotHistory.length - 1 && <TimelineConnector />}
                      </TimelineSeparator>
                      <TimelineContent>
                        <Paper elevation={3} sx={{ p: 2 }}>
                          <Typography variant="h6" component="span">
                            {history.processName}
                          </Typography>
                          <Typography>Equipment: {history.equipmentName}</Typography>
                          <Typography>
                            Input: {history.inputQuantity} | Output: {history.outputQuantity} | 
                            Defects: {history.defectQuantity || 0}
                          </Typography>
                          {history.operator && (
                            <Typography variant="caption">
                              Operator: {history.operator}
                            </Typography>
                          )}
                        </Paper>
                      </TimelineContent>
                    </TimelineItem>
                  ))}
                </Timeline>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenHistoryDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default LotTracking;
