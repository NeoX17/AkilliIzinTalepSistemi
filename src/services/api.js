import axios from 'axios';

const API_URL = 'http://localhost:5000/api'; // Backend API URL'si

// Mock data
const mockRequests = [
  {
    id: 1,
    employeeId: 'emp1',
    employeeName: 'Ahmet Yılmaz',
    startDate: '2024-03-01',
    endDate: '2024-03-05',
    reason: 'Aile ziyareti',
    status: 'pending'
  },
  {
    id: 2,
    employeeId: 'emp2',
    employeeName: 'Ayşe Demir',
    startDate: '2024-03-10',
    endDate: '2024-03-15',
    reason: 'Sağlık kontrolü',
    status: 'approved'
  }
];

// Simulate API delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Auth API
export const login = async (credentials) => {
  await delay(500);
  
  // Mock login validation
  if (credentials.email === 'hr@example.com' && credentials.password === 'hr123') {
    return {
      data: {
        token: 'mock-token-hr',
        role: 'hr'
      }
    };
  } else if (credentials.email === 'employee@example.com' && credentials.password === 'emp123') {
    return {
      data: {
        token: 'mock-token-employee',
        role: 'employee'
      }
    };
  }
  
  throw {
    response: {
      data: {
        message: 'Geçersiz email veya şifre'
      }
    }
  };
};

// Leave Request API
export const getAllLeaveRequests = async () => {
  await delay(500);
  return { data: mockRequests };
};

export const getEmployeeLeaveRequests = async (employeeId) => {
  await delay(500);
  return { 
    data: mockRequests.filter(request => request.employeeId === employeeId)
  };
};

export const createLeaveRequest = async (requestData) => {
  await delay(500);
  const newRequest = {
    id: mockRequests.length + 1,
    employeeId: 'current-user',
    employeeName: 'Mevcut Kullanıcı',
    ...requestData,
    status: 'pending'
  };
  mockRequests.push(newRequest);
  return { data: newRequest };
};

export const updateLeaveStatus = async (requestId, newStatus) => {
  await delay(500);
  const request = mockRequests.find(r => r.id === requestId);
  if (request) {
    request.status = newStatus;
  }
  return { data: request };
};

export const analyzeLeaveRequests = async () => {
  await delay(1000);
  return {
    data: {
      totalRequests: mockRequests.length,
      approvedRequests: mockRequests.filter(r => r.status === 'approved').length,
      pendingRequests: mockRequests.filter(r => r.status === 'pending').length,
      rejectedRequests: mockRequests.filter(r => r.status === 'rejected').length,
      averageDuration: 3.5,
      mostCommonReason: 'Aile ziyareti'
    }
  };
};

