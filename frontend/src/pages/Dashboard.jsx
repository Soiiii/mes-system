import { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
  Chip,
  Paper,
} from '@mui/material';
import {
  TrendingUp,
  Warning,
  CheckCircle,
  Error,
} from '@mui/icons-material';
import { Line, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';
import { dashboardApi, equipmentApi } from '../services/api';
import useSSE from '../hooks/useSSE';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

function StatCard({ title, value, icon, color, subtitle }) {
  return (
    <Card>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Box>
            <Typography color="textSecondary" gutterBottom variant="body2">
              {title}
            </Typography>
            <Typography variant="h4" component="div">
              {value}
            </Typography>
            {subtitle && (
              <Typography variant="body2" color="textSecondary">
                {subtitle}
              </Typography>
            )}
          </Box>
          <Box
            sx={{
              backgroundColor: color,
              borderRadius: '50%',
              width: 60,
              height: 60,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}

function Dashboard() {
  const [stats, setStats] = useState({
    todayProduction: 0,
    todayDefects: 0,
    operationRate: 0,
  });
  const [equipmentStatus, setEquipmentStatus] = useState([]);
  const [processProgress, setProcessProgress] = useState([]);
  const [defectRateData, setDefectRateData] = useState(null);

  // SSE for real-time updates (if available)
  const { data: sseData } = useSSE('http://localhost:8080/api/dashboard/stream');

  useEffect(() => {
    loadDashboardData();
    loadEquipmentStatus();

    // Refresh every 30 seconds
    const interval = setInterval(() => {
      loadDashboardData();
      loadEquipmentStatus();
    }, 30000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (sseData) {
      setStats(prev => ({
        ...prev,
        ...sseData,
      }));
    }
  }, [sseData]);

  const loadDashboardData = async () => {
    try {
      const response = await dashboardApi.getStats();
      setStats(response.data);

      // Load defect rate chart data
      const defectResponse = await dashboardApi.getDefectRate();
      setDefectRateData(defectResponse.data);

      // Simulate process progress (replace with actual API)
      setProcessProgress([
        { name: 'Cutting', progress: 85, status: 'In Progress' },
        { name: 'Welding', progress: 60, status: 'In Progress' },
        { name: 'Painting', progress: 30, status: 'In Progress' },
        { name: 'Assembly', progress: 0, status: 'Pending' },
      ]);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    }
  };

  const loadEquipmentStatus = async () => {
    try {
      const response = await equipmentApi.getStatus();
      setEquipmentStatus(response.data);
    } catch (error) {
      console.error('Failed to load equipment status:', error);
    }
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

  // Chart data
  const defectChartData = defectRateData ? {
    labels: ['Good', 'Defects'],
    datasets: [
      {
        data: [
          defectRateData.goodCount || 0,
          defectRateData.defectCount || 0,
        ],
        backgroundColor: ['#4caf50', '#f44336'],
        borderWidth: 1,
      },
    ],
  } : null;

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>

      <Grid container spacing={3}>
        {/* Stat Cards */}
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Today's Production"
            value={stats.todayProduction || 0}
            subtitle="units"
            icon={<TrendingUp />}
            color="#1976d2"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Today's Defects"
            value={stats.todayDefects || 0}
            subtitle="units"
            icon={<Warning />}
            color="#f44336"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          <StatCard
            title="Operation Rate"
            value={`${stats.operationRate || 0}%`}
            icon={<CheckCircle />}
            color="#4caf50"
          />
        </Grid>

        {/* Process Progress */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Process Progress
              </Typography>
              {processProgress.map((process, index) => (
                <Box key={index} mb={2}>
                  <Box display="flex" justifyContent="space-between" mb={1}>
                    <Typography variant="body2">{process.name}</Typography>
                    <Typography variant="body2" color="textSecondary">
                      {process.progress}%
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={process.progress}
                    sx={{ height: 8, borderRadius: 4 }}
                  />
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>

        {/* Defect Rate Chart */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Defect Rate
              </Typography>
              {defectChartData && (
                <Box sx={{ height: 250, display: 'flex', justifyContent: 'center' }}>
                  <Doughnut data={defectChartData} options={{ maintainAspectRatio: false }} />
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Equipment Status */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Equipment Status
              </Typography>
              <Grid container spacing={2}>
                {equipmentStatus.map((equipment) => (
                  <Grid item xs={12} sm={6} md={3} key={equipment.id}>
                    <Paper
                      elevation={2}
                      sx={{
                        p: 2,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                      }}
                    >
                      <Typography variant="subtitle1" gutterBottom>
                        {equipment.name}
                      </Typography>
                      <Chip
                        icon={getStatusIcon(equipment.status)}
                        label={equipment.status}
                        color={getStatusColor(equipment.status)}
                        sx={{ mt: 1 }}
                      />
                      {equipment.currentWorkOrder && (
                        <Typography variant="caption" color="textSecondary" sx={{ mt: 1 }}>
                          WO: {equipment.currentWorkOrder}
                        </Typography>
                      )}
                    </Paper>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Dashboard;