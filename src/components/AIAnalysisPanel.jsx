import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  LinearProgress,
  Divider
} from '@mui/material';

const AIAnalysisPanel = ({ analysis }) => {
  if (!analysis) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          AI Analiz
        </Typography>
        <Typography color="text.secondary">
          Analiz sonuçları yükleniyor...
        </Typography>
      </Box>
    );
  }

  const {
    totalRequests,
    approvedRequests,
    pendingRequests,
    rejectedRequests,
    averageDuration,
    mostCommonReason
  } = analysis;

  const approvalRate = (approvedRequests / totalRequests) * 100;
  const rejectionRate = (rejectedRequests / totalRequests) * 100;
  const pendingRate = (pendingRequests / totalRequests) * 100;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>
        AI Analiz Sonuçları
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                İzin Talepleri Dağılımı
              </Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Onaylanan: {approvedRequests} ({approvalRate.toFixed(1)}%)
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={approvalRate}
                  color="success"
                  sx={{ mb: 1 }}
                />
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  Reddedilen: {rejectedRequests} ({rejectionRate.toFixed(1)}%)
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={rejectionRate}
                  color="error"
                  sx={{ mb: 1 }}
                />
              </Box>
              <Box>
                <Typography variant="body2" color="text.secondary">
                  Bekleyen: {pendingRequests} ({pendingRate.toFixed(1)}%)
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={pendingRate}
                  color="warning"
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                İstatistikler
              </Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body1">
                  Toplam Talep: {totalRequests}
                </Typography>
              </Box>
              <Divider sx={{ my: 2 }} />
              <Box sx={{ mb: 2 }}>
                <Typography variant="body1">
                  Ortalama İzin Süresi: {averageDuration} gün
                </Typography>
              </Box>
              <Divider sx={{ my: 2 }} />
              <Box>
                <Typography variant="body1">
                  En Yaygın İzin Nedeni: {mostCommonReason}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AIAnalysisPanel;
