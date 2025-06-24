import { useState } from 'react';
import { Card, Radio, Button, message } from 'antd';
import MomoPayment from './MomoPayment';

const packages = [
    { id: 'basic', name: 'Basic Package', price: 199000, description: 'Truy cập 1 tháng, luyện đề cơ bản' },
    { id: 'pro', name: 'Pro Package', price: 399000, description: 'Truy cập 3 tháng, bao gồm chiến lược nâng band' },
    { id: 'vip', name: 'VIP Package', price: 599000, description: 'Truy cập 6 tháng, kèm mentor hỗ trợ' },
];

const PaymentPage = () => {
    const [selectedPackage, setSelectedPackage] = useState<string | null>(null);
    const [paymentMethod, setPaymentMethod] = useState('momo');
    const [showMomo, setShowMomo] = useState(false);

    const selectedPkg = packages.find((pkg) => pkg.id === selectedPackage);

    const handlePayment = () => {
        if (!selectedPkg) {
            message.warning('Vui lòng chọn gói học');
            return;
        }

        if (paymentMethod === 'momo') {
            setShowMomo(true); // trigger MomoPayment
        } else if (paymentMethod === 'bank') {
            message.info('Vui lòng chuyển khoản ngân hàng tới STK 0123456789 - IELTS Center');
        }
    };

    return (
        <div style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
            <h1>Chọn gói học</h1>
            <div style={{ display: 'flex', gap: 20, flexWrap: 'wrap' }}>
                {packages.map((pkg) => (
                    <Card
                        key={pkg.id}
                        title={pkg.name}
                        variant="outlined"
                        style={{
                            width: 240,
                            border: selectedPackage === pkg.id ? '2px solid #1890ff' : undefined,
                            cursor: 'pointer'
                        }}
                        onClick={() => {
                            setSelectedPackage(pkg.id);
                            setShowMomo(false); // reset MoMo mỗi khi đổi gói
                        }}
                    >
                        <p>{pkg.description}</p>
                        <strong>{pkg.price.toLocaleString()} VNĐ</strong>
                    </Card>
                ))}
            </div>

            <h2 style={{ marginTop: 30 }}>Chọn phương thức thanh toán</h2>
            <Radio.Group
                onChange={(e) => {
                    setPaymentMethod(e.target.value);
                    setShowMomo(false); // reset khi đổi phương thức
                }}
                value={paymentMethod}
            >
                <Radio value="momo">Thanh toán bằng MoMo</Radio>
                <Radio value="bank">Chuyển khoản ngân hàng</Radio>
            </Radio.Group>

            <div style={{ marginTop: 30 }}>
                <Button type="primary" onClick={handlePayment}>
                    Thanh toán
                </Button>
            </div>

            {paymentMethod === 'momo' && showMomo && selectedPkg && (
                <MomoPayment
                    amount={selectedPkg.price}
                    orderInfo={`Thanh toán gói ${selectedPkg.name}`}
                    onSuccess={() => {
                        message.success('Thanh toán thành công!');
                        setShowMomo(false);
                    }}
                />
            )}
        </div>
    );
};

export default PaymentPage;
