import { useEffect, useState } from 'react';
import axios from 'axios';

export default function AcceptTestPage() {
  const [tests, setTests] = useState([]);
  const [detail, setDetail] = useState<any>(null);

  useEffect(() => {
    axios.get('/api/manager/request-tests').then(res => setTests(res.data));
  }, []);

  const viewDetail = (testId: string) => {
    axios.get(`/api/manager/request-test/${testId}`).then(res => setDetail(res.data));
  };

  const acceptTest = async (testId: string) => {
    await axios.post(`/api/manager/accept-test/${testId}`);
    setTests(tests.filter((t: any) => t.testId !== testId));
    setDetail(null);
    alert('Đã duyệt đề thành công!');
  };

  const deleteTest = async (testId: string) => {
    await axios.delete(`/api/manager/request-test/${testId}`);
    setTests(tests.filter((t: any) => t.testId !== testId));
    setDetail(null);
    alert('Đã xóa đề!');
  };

  return (
    <div className="max-w-5xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Danh sách đề chờ duyệt</h1>
      <table className="w-full border mb-8">
        <thead>
          <tr>
            <th>TestId</th>
            <th>Title</th>
            <th>Tags</th>
            <th>Ngày tạo</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {tests.map((test: any) => (
            <tr key={test.testId}>
              <td>{test.testId}</td>
              <td>{test.testTitle}</td>
              <td>{test.tags?.join(', ')}</td>
              <td>{test.createAt}</td>
              <td>
                <button className="text-blue-600 underline" onClick={() => viewDetail(test.testId)}>
                  View Detail
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {detail && (
        <div className="bg-white rounded-xl shadow-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Chi tiết đề: {detail.test?.testTitle}</h2>
          <div className="flex gap-4 mb-4">
            <button className="bg-green-600 text-white px-4 py-2 rounded" onClick={() => acceptTest(detail.test.testId)}>
              Accept
            </button>
            <button className="bg-red-600 text-white px-4 py-2 rounded" onClick={() => deleteTest(detail.test.testId)}>
              Delete
            </button>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-bold text-lg mb-2">Listening</h3>
              <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(detail.listening, null, 2)}</pre>
            </div>
            <div>
              <h3 className="font-bold text-lg mb-2">Reading</h3>
              <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(detail.reading, null, 2)}</pre>
            </div>
            <div>
              <h3 className="font-bold text-lg mb-2">Writing</h3>
              <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(detail.writing, null, 2)}</pre>
            </div>
            <div>
              <h3 className="font-bold text-lg mb-2">Speaking</h3>
              <pre className="bg-gray-100 p-2 rounded">{JSON.stringify(detail.speaking, null, 2)}</pre>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}