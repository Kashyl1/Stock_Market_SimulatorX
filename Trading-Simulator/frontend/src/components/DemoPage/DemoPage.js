// src/DemoPage.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const DemoPage = () => {
    const [message, setMessage] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get('/api/demo-controller');
                setMessage(response.data);
            } catch (error) {
                console.error('Error fetching data: ', error);
            }
        };

        fetchData();
    }, []);

    return <div>{message}</div>;
};

export default DemoPage;