import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
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
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  PlayArrow as PlayIcon,
  Visibility as ViewIcon,
} from '@mui/icons-material';
import { workOrdersApi, productsApi, routingApi } from '../services/api';
import { format } from 'date-fns';

function WorkOrders() {
  const [workOrders, setWorkOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [openRoutingDialog, setOpenRoutingDialog] = useState(false);
  const [currentWorkOrder, setCurrentWorkOrder] = useState(null);
  const [routing, setRouting] = useState([]);
  const [formData, setFormData] = useState({
    productId: '',
    quantity: '',
    plannedStartDate: '',
    plannedEndDate: '',
  });

  useEffect(() => {
    loadWorkOrders();
    loadProducts();
  }, []);

  const loadWorkOrders = async () => {
    try {
      const response = await workOrdersApi.getAll();
      setWorkOrders(response.data);
    } catch (error) {
      console.error('Failed to load work orders:', error);
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

  const loadRouting = async (productId) => {
    try {
      const response = await routingApi.getByProduct(productId);
      setRouting(response.data);
    } catch (error) {
      console.error('Failed to load routing:', error);
      setRouting([]);
    }
  };

  const handleOpenDialog = (workOrder = null) => {
    if (workOrder) {
      setCurrentWorkOrder(workOrder);
      setFormData({
        productId: workOrder.product.id,
        quantity: workOrder.quantity,
        plannedStartDate: workOrder.plannedStartDate,
        plannedEndDate: workOrder.plannedEndDate,
      });
    } else {
      setCurrentWorkOrder(null);
      setFormData({
        productId: '',
        quantity: '',
        plannedStartDate: '',
        plannedEndDate: '',
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setCurrentWorkOrder(null);
  };

  const handleSubmit = async () => {
    try {
      if (currentWorkOrder) {
        await workOrdersApi.update(currentWorkOrder.id, formData);
      } else {
        await workOrdersApi.create(formData);
      }
      loadWorkOrders();
      handleCloseDialog();
    } catch (error) {
      console.error('Failed to save work order:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this work order?')) {
      try {
        await workOrdersApi.delete(id);
        loadWorkOrders();
      } catch (error) {
        console.error('Failed to delete work order:', error);
      }
    }
  };

  const handleStart = async (id) => {
    try {
      await workOrdersApi.start(id);
      loadWorkOrders();
    } catch (error) {
      console.error('Failed to start work order:', error);
    }
  };

  const handleViewRouting = async (workOrder) => {
    setCurrentWorkOrder(workOrder);
    await loadRouting(workOrder.product.id);
    setOpenRoutingDialog(true);
  };

  const getStatusColor = (status) => {
    const colors = {
      PENDING: 'default',
      IN_PROGRESS: 'primary',
      COMPLETED: 'success',
      CANCELLED: 'error',
    };
    return colors[status] || 'default';
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Work Orders</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          New Work Order
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Work Order ID</TableCell>
              <TableCell>Product</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Planned Start</TableCell>
              <TableCell>Planned End</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {workOrders.map((order) => (
              <TableRow key={order.id}>
                <TableCell>{order.workOrderNumber || order.id}</TableCell>
                <TableCell>{order.product?.name || 'N/A'}</TableCell>
                <TableCell>{order.quantity}</TableCell>
                <TableCell>
                  <Chip
                    label={order.status}
                    color={getStatusColor(order.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {order.plannedStartDate
                    ? format(new Date(order.plannedStartDate), 'yyyy-MM-dd')
                    : 'N/A'}
                </TableCell>
                <TableCell>
                  {order.plannedEndDate
                    ? format(new Date(order.plannedEndDate), 'yyyy-MM-dd')
                    : 'N/A'}
                </TableCell>
                <TableCell>
                  {order.status === 'PENDING' && (
                    <IconButton
                      size="small"
                      color="primary"
                      onClick={() => handleStart(order.id)}
                      title="Start"
                    >
                      <PlayIcon />
                    </IconButton>
                  )}
                  <IconButton
                    size="small"
                    onClick={() => handleViewRouting(order)}
                    title="View Routing"
                  >
                    <ViewIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => handleOpenDialog(order)}
                    title="Edit"
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={() => handleDelete(order.id)}
                    title="Delete"
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Create/Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {currentWorkOrder ? 'Edit Work Order' : 'New Work Order'}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <FormControl fullWidth>
              <InputLabel>Product</InputLabel>
              <Select
                value={formData.productId}
                label="Product"
                onChange={(e) =>
                  setFormData({ ...formData, productId: e.target.value })
                }
              >
                {products.map((product) => (
                  <MenuItem key={product.id} value={product.id}>
                    {product.name}
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
              label="Planned Start Date"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              value={formData.plannedStartDate}
              onChange={(e) =>
                setFormData({ ...formData, plannedStartDate: e.target.value })
              }
            />
            <TextField
              label="Planned End Date"
              type="date"
              fullWidth
              InputLabelProps={{ shrink: true }}
              value={formData.plannedEndDate}
              onChange={(e) =>
                setFormData({ ...formData, plannedEndDate: e.target.value })
              }
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {currentWorkOrder ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Routing Dialog */}
      <Dialog
        open={openRoutingDialog}
        onClose={() => setOpenRoutingDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Process Routing - {currentWorkOrder?.product?.name}
        </DialogTitle>
        <DialogContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Sequence</TableCell>
                  <TableCell>Process</TableCell>
                  <TableCell>Equipment</TableCell>
                  <TableCell>Standard Time (min)</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {routing.map((route) => (
                  <TableRow key={route.id}>
                    <TableCell>{route.sequence}</TableCell>
                    <TableCell>{route.process?.name || 'N/A'}</TableCell>
                    <TableCell>{route.equipment?.name || 'Any'}</TableCell>
                    <TableCell>{route.standardTime || 'N/A'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenRoutingDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default WorkOrders;