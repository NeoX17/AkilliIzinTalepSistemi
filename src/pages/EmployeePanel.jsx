import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  TextField,
  Alert,
  CircularProgress
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers';
import { getAllLeaveRequests, createLeaveRequest } from '../services/api';

const EmployeePanel = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    startDate: null,
    endDate: null,
    reason: ''
  });

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const response = await getAllLeaveRequests();
      setRequests(response.data);
      setError('');
    } catch (err) {
      setError('İzin talepleri yüklenirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await createLeaveRequest(formData);
      setFormData({
        startDate: null,
        endDate: null,
        reason: ''
      });
      await fetchRequests();
      setError('');
    } catch (err) {
      setError('İzin talebi oluşturulurken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        İzin Talepleri
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Yeni İzin Talebi
          </Typography>
          <form onSubmit={handleSubmit}>
            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
              <DatePicker
                label="Başlangıç Tarihi"
                value={formData.startDate}
                onChange={(newValue) => setFormData({ ...formData, startDate: newValue })}
                sx={{ flex: 1 }}
              />
              <DatePicker
                label="Bitiş Tarihi"
                value={formData.endDate}
                onChange={(newValue) => setFormData({ ...formData, endDate: newValue })}
                sx={{ flex: 1 }}
              />
            </Box>
            <TextField
              fullWidth
              label="İzin Nedeni"
              multiline
              rows={3}
              value={formData.reason}
              onChange={(e) => setFormData({ ...formData, reason: e.target.value })}
              sx={{ mb: 2 }}
            />
            <Button
              type="submit"
              variant="contained"
              disabled={loading}
            >
              {loading ? <CircularProgress size={24} /> : 'İzin Talebi Oluştur'}
            </Button>
          </form>
        </CardContent>
      </Card>

      <Typography variant="h6" gutterBottom>
        İzin Geçmişi
      </Typography>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        requests.map((request) => (
          <Card key={request.id} sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="subtitle1">
                {new Date(request.startDate).toLocaleDateString()} - {new Date(request.endDate).toLocaleDateString()}
              </Typography>
              <Typography color="text.secondary" gutterBottom>
                {request.reason}
              </Typography>
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
            </CardContent>
          </Card>
        ))
      )}
    </Box>
  );
};

export default EmployeePanel;