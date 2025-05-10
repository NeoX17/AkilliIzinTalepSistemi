import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Tabs, Tab, CircularProgress, Snackbar, Alert } from '@mui/material';
import HRRequestList from '../components/HRRequestList';
import AIAnalysisPanel from '../components/AIAnalysisPanel';
import Dashboard from '../components/Dashboard';
import { getAllLeaveRequests, analyzeLeaveRequests, updateLeaveStatus } from '../services/api';

const HRPanel = () => {
  const [requests, setRequests] = useState([]);
  const [analysis, setAnalysis] = useState(null);
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRequests = async () => {
      setLoading(true);
      try {
        const response = await getAllLeaveRequests();
        setRequests(response.data);
        setError(null);
      } catch (err) {
        console.error('İzin talepleri yüklenirken hata oluştu:', err);
        setError('İzin talepleri yüklenirken bir hata oluştu. Lütfen tekrar deneyin.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchRequests();
  }, []);

  const handleAnalyze = async () => {
    setLoading(true);
    try {
      const response = await analyzeLeaveRequests();
      setAnalysis(response.data);
      setTabValue(1);
      setError(null);
    } catch (err) {
      console.error('Analiz yapılırken hata oluştu:', err);
      setError('Analiz yapılırken bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  const handleStatusChange = async (requestId, newStatus) => {
    setLoading(true);
    try {
      await updateLeaveStatus(requestId, newStatus);
      const response = await getAllLeaveRequests();
      setRequests(response.data);
      setError(null);
    } catch (err) {
      console.error('Durum güncellenirken hata oluştu:', err);
      setError('Durum güncellenirken bir hata oluştu. Lütfen tekrar deneyin.');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseError = () => {
    setError(null);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4">İK Yönetim Paneli</Typography>
        <Button 
          variant="contained" 
          color="secondary" 
          onClick={handleLogout}
        >
          Çıkış Yap
        </Button>
      </Box>

      <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
        <Tab label="Tüm Talepler" />
        <Tab label="AI Analiz" />
        <Tab label="Dashboard" />
      </Tabs>

      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {!loading && tabValue === 0 && (
        <HRRequestList 
          requests={requests} 
          onAnalyze={handleAnalyze}
          onStatusChange={handleStatusChange}
        />
      )}
      
      {!loading && tabValue === 1 && (
        <AIAnalysisPanel analysis={analysis} />
      )}
      
      {!loading && tabValue === 2 && (
        <Dashboard requests={requests} />
      )}

      <Snackbar 
        open={!!error} 
        autoHideDuration={6000} 
        onClose={handleCloseError}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={handleCloseError} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default HRPanel;