import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { message, Result } from 'antd';
import axios from 'axios';

const PaymentCallback = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const searchParams = new URLSearchParams(location.search);

    useEffect(() => {
        const checkPaymentStatus = async () => {
            const orderId = searchParams.get('orderId');
            if (orderId) {
                try {
                    const response = await axios.get(`http://localhost:8080/api/payment/status/${orderId}`);
                    if (response.data.resultCode === 0) {
                        message.success('Payment successful!');
                        // Redirect to success page or home page after 3 seconds
                        setTimeout(() => {
                            navigate('/payment-success');
                        }, 3000);
                    } else {
                        message.error('Payment failed: ' + response.data.message);
                        // Redirect to error page or home page after 3 seconds
                        setTimeout(() => {
                            navigate('/payment-error');
                        }, 3000);
                    }
                } catch (error) {
                    message.error('Error checking payment status: ' + error.message);
                    setTimeout(() => {
                        navigate('/');
                    }, 3000);
                }
            }
        };

        checkPaymentStatus();
    }, [location, navigate]);

    return (
        <Result
            status="info"
            title="Processing Payment"
            subTitle="Please wait while we process your payment..."
        />
    );
};

export default PaymentCallback; 