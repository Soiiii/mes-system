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
  Grid,
  Card,
  CardContent,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { defectsApi } from '../services/api';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

function Defects() {
  const [defects, setDefects] = useState([]);
  const [stats, setStats] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [currentDefect, setCurrentDefect] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
  });

  useEffect(() => {
    loadDefects();
    loadStats();
  }, []);

  const loadDefects = async () => {
    try {
      const response = await defectsApi.getAll();
      setDefects(response.data);
    } catch (error) {
      console.error('Failed to load defects:', error);
    }
  };

  const loadStats = async () => {
    try {
      const response = await defectsApi.getStats();
      setStats(response.data);
    } catch (error) {
      console.error('Failed to load stats:', error);
    }
  };

  const handleOpenDialog = (defect = null) => {
    if (defect) {
      setCurrentDefect(defect);
      setFormData({
        code: defect.code,
        name: defect.name,
        description: defect.description || '',
      });
    } else {
      setCurrentDefect(null);
      setFormData({
        code: '',
        name: '',
        description: '',
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setCurrentDefect(null);
  };

  const handleSubmit = async () => {
    try {
      if (currentDefect) {
        await defectsApi.update(currentDefect.id, formData);
      } else {
        await defectsApi.create(formData);
      }
      loadDefects();
      loadStats();
      handleCloseDialog();
    } catch (error) {
      console.error('Failed to save defect:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this defect code?')) {
      try {
        await defectsApi.delete(id);
        loadDefects();
        loadStats();
      } catch (error) {
        console.error('Failed to delete defect:', error);
      }
    }
  };

  const chartData = stats
    ? {
        labels: stats.map((item) => item.defectName || item.defectCode),
        datasets: [
          {
            label: 'Defect Count',
            data: stats.map((item) => item.count),
            backgroundColor: 'rgba(244, 67, 54, 0.6)',
            borderColor: 'rgba(244, 67, 54, 1)',
            borderWidth: 1,
          },
        ],
      }
    : null;

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Defect Statistics by Type',
      },
    },
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Defect Management
      </Typography>

      <Grid container spacing={3}>
        {/* Statistics Chart */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Defect Statistics
              </Typography>
              {chartData && (
                <Box sx={{ height: 400 }}>
                  <Bar data={chartData} options={chartOptions} />
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Defect Codes Table */}
        <Grid item xs={12}>
          <Box
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            mb={2}
          >
            <Typography variant="h5">Defect Codes</Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => handleOpenDialog()}
            >
              New Defect Code
            </Button>
          </Box>

          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Defect Code</TableCell>
                  <TableCell>Defect Name</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {defects.map((defect) => (
                  <TableRow key={defect.id}>
                    <TableCell>{defect.code}</TableCell>
                    <TableCell>{defect.name}</TableCell>
                    <TableCell>{defect.description || 'N/A'}</TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        onClick={() => handleOpenDialog(defect)}
                        title="Edit"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => handleDelete(defect.id)}
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
        </Grid>
      </Grid>

      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {currentDefect ? 'Edit Defect Code' : 'New Defect Code'}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Defect Code"
              fullWidth
              value={formData.code}
              onChange={(e) =>
                setFormData({ ...formData, code: e.target.value })
              }
            />
            <TextField
              label="Defect Name"
              fullWidth
              value={formData.name}
              onChange={(e) =>
                setFormData({ ...formData, name: e.target.value })
              }
            />
            <TextField
              label="Description"
              fullWidth
              multiline
              rows={3}
              value={formData.description}
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {currentDefect ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default Defects;