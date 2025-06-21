import { useEffect, useState } from "react";

interface AddTest {
  id: string;
  testId: string;
  testTitle: string;
  tags: string[];
  createAt: string;
}

export default function AcceptTestPage() {
  const [tests, setTests] = useState<AddTest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/manager/request-tests", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Không thể tải danh sách đề thi.");
        return res.json();
      })
      .then((data) => {
        setTests(data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  }, []);

  const acceptTest = async (testId: string) => {
    const confirm = window.confirm("Bạn có chắc muốn duyệt đề này không?");
    if (!confirm) return;

    try {
      const res = await fetch(`http://localhost:8080/api/manager/accept-test/${testId}`, {
        method: "POST",
        credentials: "include",
      });

      if (!res.ok) throw new Error("Duyệt đề thất bại");

      alert("Duyệt thành công!");
      setTests(tests.filter((test) => test.testId !== testId));
    } catch (err) {
      alert("Duyệt thất bại!");
      console.error(err);
    }
  };

  if (loading) return <div className="text-center p-10 text-gray-500">Đang tải dữ liệu...</div>;
  if (error) return <div className="text-center p-10 text-red-500">{error}</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Danh sách đề thi chờ duyệt</h1>
      <div className="grid gap-4">
        {tests.length === 0 ? (
          <p className="text-gray-500">Không có đề nào đang chờ duyệt.</p>
        ) : (
          tests.map((test) => (
            <div
              key={test.testId}
              className="bg-white shadow-md rounded-xl p-4 border border-gray-200 flex flex-col md:flex-row justify-between items-start md:items-center"
            >
              <div>
                <h2 className="text-lg font-semibold">{test.testTitle}</h2>
                <p className="text-sm text-gray-500">Mã đề: {test.testId}</p>
                <p className="text-sm text-gray-500">Ngày tạo: {new Date(test.createAt).toLocaleString()}</p>
                <div className="flex gap-2 mt-2 flex-wrap">
                  {test.tags?.map((tag, idx) => (
                    <span
                      key={idx}
                      className="bg-blue-100 text-blue-800 text-xs font-medium px-2 py-1 rounded-full"
                    >
                      {tag}
                    </span>
                  ))}
                </div>
              </div>
              <div className="mt-4 md:mt-0 flex gap-3">
                <a
                  href={`/request-test-detail/${test.testId}`}
                  className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-4 py-2 rounded-lg text-sm"
                >
                  Xem chi tiết
                </a>
                <button
                  onClick={() => acceptTest(test.testId)}
                  className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg text-sm"
                >
                  Duyệt đề
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
