import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  ButtonGroup,
  CircularProgress
} from '@mui/material';

const HRRequestList = ({ requests, onStatusChange, onAnalyze }) => {
  const handleStatusChange = async (requestId, newStatus) => {
    await onStatusChange(requestId, newStatus);
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5">İzin Talepleri</Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={onAnalyze}
        >
          AI Analiz
        </Button>
      </Box>

      {requests.map((request) => (
        <Card key={request.id} sx={{ mb: 2 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {request.employeeName}
            </Typography>
            <Typography color="text.secondary" gutterBottom>
              {new Date(request.startDate).toLocaleDateString()} - {new Date(request.endDate).toLocaleDateString()}
            </Typography>
            <Typography paragraph>
              {request.reason}
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography
                color={
                  request.status === 'approved'
                    ? 'success.main'
                    : request.status === 'rejected'
                    ? 'error.main'
                    : 'warning.main'
                }
              >
                Durum: {request.status === 'approved' ? 'Onaylandı' : request.status === 'rejected' ? 'Reddedildi' : 'Beklemede'}
              </Typography>
              {request.status === 'pending' && (
                <ButtonGroup>
                  <Button
                    color="success"
                    onClick={() => handleStatusChange(request.id, 'approved')}
                  >
                    Onayla
                  </Button>
                  <Button
                    color="error"
                    onClick={() => handleStatusChange(request.id, 'rejected')}
                  >
                    Reddet
                  </Button>
                </ButtonGroup>
              )}
            </Box>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export default HRRequestList;
