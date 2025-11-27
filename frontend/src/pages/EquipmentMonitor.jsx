import { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Chip,
  Paper,
} from '@mui/material';
import {
  CheckCircle,
  Warning,
  Error,
} from '@mui/icons-material';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { equipmentApi } from '../services/api';
import useSSE from '../hooks/useSSE';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

function EquipmentMonitor() {
  const [equipment, setEquipment] = useState([]);
  const [selectedEquipment, setSelectedEquipment] = useState(null);
  const [chartData, setChartData] = useState({
    temperature: { labels: [], data: [] },
    vibration: { labels: [], data: [] },
    pressure: { labels: [], data: [] },
  });

  // SSE for real-time equipment data
  const { data: sseData } = useSSE(
    selectedEquipment
      ? `http://localhost:8080/api/equipment/${selectedEquipment.id}/stream`
      : null
  );

  useEffect(() => {
    loadEquipment();
  }, []);

  useEffect(() => {
    if (sseData) {
      updateChartData(sseData);
    }
  }, [sseData]);

  const loadEquipment = async () => {
    try {
      const response = await equipmentApi.getAll();
      setEquipment(response.data);
      if (response.data.length > 0) {
        setSelectedEquipment(response.data[0]);
      }
    } catch (error) {
      console.error('Failed to load equipment:', error);
    }
  };

  const updateChartData = (data) => {
    const now = new Date().toLocaleTimeString();
    const maxDataPoints = 20;

    setChartData((prev) => {
      const updateMetric = (metric, value) => {
        const newLabels = [...prev[metric].labels, now].slice(-maxDataPoints);
        const newData = [...prev[metric].data, value].slice(-maxDataPoints);
        return { labels: newLabels, data: newData };
      };

      return {
        temperature: updateMetric('temperature', data.temperature),
        vibration: updateMetric('vibration', data.vibration),
        pressure: updateMetric('pressure', data.pressure),
      };
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'RUN':
        return 'success';
      case 'IDLE':
        return 'warning';
      case 'ALARM':
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'RUN':
        return <CheckCircle />;
      case 'IDLE':
        return <Warning />;
      case 'ALARM':
        return <Error />;
      default:
        return null;
    }
  };

  const createChartConfig = (label, data, color) => ({
    labels: data.labels,
    datasets: [
      {
        label,
        data: data.data,
        borderColor: color,
        backgroundColor: `${color}33`,
        tension: 0.4,
      },
    ],
  });

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Equipment Monitor
      </Typography>

      <Grid container spacing={3}>
        {/* Equipment Status Cards */}
        <Grid item xs={12}>
          <Grid container spacing={2}>
            {equipment.map((eq) => (
              <Grid item xs={12} sm={6} md={3} key={eq.id}>
                <Card
                  sx={{
                    cursor: 'pointer',
                    border:
                      selectedEquipment?.id === eq.id
                        ? '2px solid #1976d2'
                        : '2px solid transparent',
                  }}
                  onClick={() => setSelectedEquipment(eq)}
                >
                  <CardContent>
                    <Box
                      display="flex"
                      flexDirection="column"
                      alignItems="center"
                      gap={1}
                    >
                      <Typography variant="h6">{eq.name}</Typography>
                      <Chip
                        icon={getStatusIcon(eq.status)}
                        label={eq.status}
                        color={getStatusColor(eq.status)}
                      />
                      {eq.currentWorkOrder && (
                        <Typography variant="caption" color="textSecondary">
                          WO: {eq.currentWorkOrder}
                        </Typography>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Grid>

        {/* Real-time Charts */}
        {selectedEquipment && (
          <>
            <Grid item xs={12}>
              <Typography variant="h5" gutterBottom>
                {selectedEquipment.name} - Real-time Metrics
              </Typography>
            </Grid>

            <Grid item xs={12} md={4}>
              <Paper sx={{ p: 2, height: 300 }}>
                <Typography variant="h6" gutterBottom>
                  Temperature (°C)
                </Typography>
                <Box sx={{ height: 'calc(100% - 40px)' }}>
                  <Line
                    data={createChartConfig(
                      'Temperature',
                      chartData.temperature,
                      '#f44336'
                    )}
                    options={chartOptions}
                  />
                </Box>
              </Paper>
            </Grid>

            <Grid item xs={12} md={4}>
              <Paper sx={{ p: 2, height: 300 }}>
                <Typography variant="h6" gutterBottom>
                  Vibration (Hz)
                </Typography>
                <Box sx={{ height: 'calc(100% - 40px)' }}>
                  <Line
                    data={createChartConfig(
                      'Vibration',
                      chartData.vibration,
                      '#ff9800'
                    )}
                    options={chartOptions}
                  />
                </Box>
              </Paper>
            </Grid>

            <Grid item xs={12} md={4}>
              <Paper sx={{ p: 2, height: 300 }}>
                <Typography variant="h6" gutterBottom>
                  Pressure (bar)
                </Typography>
                <Box sx={{ height: 'calc(100% - 40px)' }}>
                  <Line
                    data={createChartConfig(
                      'Pressure',
                      chartData.pressure,
                      '#4caf50'
                    )}
                    options={chartOptions}
                  />
                </Box>
              </Paper>
            </Grid>
          </>
        )}
      </Grid>
    </Box>
  );
}

export default EquipmentMonitor;