import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  LinearProgress,
  Chip,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
  Inventory as InventoryIcon,
  Assessment as AssessmentIcon,
} from '@mui/icons-material';
import { statisticsApi } from '../services/api';

function Statistics() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStatistics();
    // 30초마다 자동 갱신
    const interval = setInterval(loadStatistics, 30000);
    return () => clearInterval(interval);
  }, []);

  const loadStatistics = async () => {
    try {
      const response = await statisticsApi.getProductionStatistics();
      setStats(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Failed to load statistics:', error);
      setLoading(false);
    }
  };

  if (loading || !stats) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <Typography>Loading statistics...</Typography>
      </Box>
    );
  }

  const getOEEColor = (oee) => {
    if (oee >= 85) return 'success';
    if (oee >= 65) return 'warning';
    return 'error';
  };

  const getOEEStatus = (oee) => {
    if (oee >= 85) return 'World Class';
    if (oee >= 65) return 'Good';
    if (oee >= 40) return 'Fair';
    return 'Poor';
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Production Statistics</Typography>
        <Chip label="Auto-refresh: 30s" size="small" color="primary" variant="outlined" />
      </Box>

      {/* LOT 통계 */}
      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total LOTs
                  </Typography>
                  <Typography variant="h3">{stats.totalLots}</Typography>
                </Box>
                <InventoryIcon sx={{ fontSize: 48, color: 'primary.main', opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Completed LOTs
                  </Typography>
                  <Typography variant="h3" color="success.main">
                    {stats.completedLots}
                  </Typography>
                </Box>
                <CheckCircleIcon sx={{ fontSize: 48, color: 'success.main', opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    In Progress
                  </Typography>
                  <Typography variant="h3" color="warning.main">
                    {stats.inProgressLots}
                  </Typography>
                </Box>
                <AssessmentIcon sx={{ fontSize: 48, color: 'warning.main', opacity: 0.3 }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* 생산량 및 불량 통계 */}
      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Production Summary
              </Typography>
              <Box mt={2}>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Total Produced</Typography>
                  <Typography variant="h6" color="success.main">
                    {stats.totalProduced}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Total Defects</Typography>
                  <Typography variant="h6" color="error.main">
                    {stats.totalDefects}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" mt={2}>
                  <Typography variant="body1" fontWeight="bold">
                    Defect Rate
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <Typography variant="h5" color={stats.overallDefectRate > 5 ? 'error' : 'success.main'}>
                      {stats.overallDefectRate}%
                    </Typography>
                    {stats.overallDefectRate > 5 ? (
                      <TrendingUpIcon color="error" />
                    ) : (
                      <TrendingDownIcon color="success" />
                    )}
                  </Box>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quality Inspection Summary
              </Typography>
              <Box mt={2}>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Total Inspections</Typography>
                  <Typography variant="h6">{stats.totalInspections}</Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Passed</Typography>
                  <Typography variant="h6" color="success.main">
                    {stats.passedInspections}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="body2">Failed</Typography>
                  <Typography variant="h6" color="error.main">
                    {stats.failedInspections}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" mt={2}>
                  <Typography variant="body1" fontWeight="bold">
                    Pass Rate
                  </Typography>
                  <Typography variant="h5" color="success.main">
                    {stats.inspectionPassRate}%
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* OEE (Overall Equipment Effectiveness) */}
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h5" gutterBottom>
                OEE (Overall Equipment Effectiveness)
              </Typography>
              <Typography variant="body2" color="textSecondary" mb={3}>
                OEE = Availability × Performance × Quality
              </Typography>

              <Grid container spacing={3}>
                {/* OEE 총점 */}
                <Grid item xs={12} md={3}>
                  <Box textAlign="center">
                    <Typography variant="h2" color={getOEEColor(stats.oee)}>
                      {stats.oee}%
                    </Typography>
                    <Typography variant="h6" color="textSecondary">
                      Overall OEE
                    </Typography>
                    <Chip
                      label={getOEEStatus(stats.oee)}
                      color={getOEEColor(stats.oee)}
                      sx={{ mt: 1 }}
                    />
                  </Box>
                </Grid>

                {/* Availability */}
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" gutterBottom>
                    Availability (가동률)
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1} mb={1}>
                    <Typography variant="h4">{stats.availability}%</Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={stats.availability}
                    sx={{ height: 10, borderRadius: 5 }}
                    color="primary"
                  />
                  <Typography variant="caption" color="textSecondary" mt={1}>
                    Planned vs Actual Operating Time
                  </Typography>
                </Grid>

                {/* Performance */}
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" gutterBottom>
                    Performance (성능률)
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1} mb={1}>
                    <Typography variant="h4">{stats.performance}%</Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={stats.performance}
                    sx={{ height: 10, borderRadius: 5 }}
                    color="info"
                  />
                  <Typography variant="caption" color="textSecondary" mt={1}>
                    Ideal vs Actual Cycle Time
                  </Typography>
                </Grid>

                {/* Quality */}
                <Grid item xs={12} md={3}>
                  <Typography variant="body2" gutterBottom>
                    Quality (품질률)
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1} mb={1}>
                    <Typography variant="h4">{stats.quality}%</Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={stats.quality}
                    sx={{ height: 10, borderRadius: 5 }}
                    color="success"
                  />
                  <Typography variant="caption" color="textSecondary" mt={1}>
                    Good Units / Total Units
                  </Typography>
                </Grid>
              </Grid>

              <Box mt={3} p={2} bgcolor="grey.100" borderRadius={1}>
                <Typography variant="body2" color="textSecondary">
                  <strong>OEE Interpretation:</strong>
                  <br />
                  • World Class (85%+): Excellent performance, benchmark level
                  <br />
                  • Good (65-84%): Acceptable performance, room for improvement
                  <br />
                  • Fair (40-64%): Needs significant improvement
                  <br />• Poor (&lt;40%): Requires immediate action
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Statistics;
