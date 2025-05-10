import React, { useState } from 'react';
import {
  Box,
  Typography,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tabs,
  Tab,
  Chip
} from '@mui/material';
import { format } from 'date-fns';

const HRRequestList = ({ requests, onStatusChange, onAnalyze }) => {
  const [activeTab, setActiveTab] = useState(0);

  const handleStatusChange = async (requestId, newStatus) => {
    await onStatusChange(requestId, newStatus);
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  // İzinleri durumlarına göre filtreleme
  const pendingRequests = requests.filter(req => req.status === 'pending');
  const approvedRequests = requests.filter(req => req.status === 'approved');
  const rejectedRequests = requests.filter(req => req.status === 'rejected');

  // Aktif sekmeye göre izin taleplerini gösterme
  const displayRequests = () => {
    switch (activeTab) {
      case 0:
        return pendingRequests;
      case 1:
        return approvedRequests;
      case 2:
        return rejectedRequests;
      default:
        return requests;
    }
  };

  const getStatusChip = (status) => {
    const statusText = status === 'approved' ? 'Onaylandı' : status === 'rejected' ? 'Reddedildi' : 'Beklemede';
    const statusColor = status === 'approved' ? 'success' : status === 'rejected' ? 'error' : 'warning';
    
    return <Chip label={statusText} color={statusColor} />;
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

      <Paper sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={handleTabChange} centered>
          <Tab label={`Bekleyen (${pendingRequests.length})`} />
          <Tab label={`Onaylanan (${approvedRequests.length})`} />
          <Tab label={`Reddedilen (${rejectedRequests.length})`} />
        </Tabs>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>İsim</TableCell>
              <TableCell>Soyisim</TableCell>
              <TableCell>E-posta</TableCell>
              <TableCell>Başlangıç</TableCell>
              <TableCell>Bitiş</TableCell>
              <TableCell>Sebep</TableCell>
              <TableCell>Durum</TableCell>
              {activeTab === 0 && <TableCell>İşlemler</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {displayRequests().map((request) => (
              <TableRow key={request.id}>
                <TableCell>{request.firstName}</TableCell>
                <TableCell>{request.lastName}</TableCell>
                <TableCell>{request.email || 'N/A'}</TableCell>
                <TableCell>{format(new Date(request.startDate), 'dd.MM.yyyy')}</TableCell>
                <TableCell>{format(new Date(request.endDate), 'dd.MM.yyyy')}</TableCell>
                <TableCell>{request.reason}</TableCell>
                <TableCell>{getStatusChip(request.status)}</TableCell>
                {activeTab === 0 && (
                  <TableCell>
                    <Button 
                      size="small" 
                      color="success" 
                      variant="contained"
                      onClick={() => handleStatusChange(request.id, 'approved')}
                      sx={{ mr: 1 }}
                    >
                      Onayla
                    </Button>
                    <Button 
                      size="small" 
                      color="error" 
                      variant="contained"
                      onClick={() => handleStatusChange(request.id, 'rejected')}
                    >
                      Reddet
                    </Button>
                  </TableCell>
                )}
              </TableRow>
            ))}
            {displayRequests().length === 0 && (
              <TableRow>
                <TableCell colSpan={activeTab === 0 ? 7 : 6} align="center">
                  Bu kategoride izin talebi bulunmamaktadır.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default HRRequestList;
