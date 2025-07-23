"use client"

import {useEffect, useState} from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
  Search,
  Filter,
  Download,
  FileText,
  Printer,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  CalendarIcon,
  DollarSign,
  TrendingUp,
  Users,
  CreditCard,
  Eye,
  RefreshCw,
} from "lucide-react"
import { format } from "date-fns"
import { vi } from "date-fns/locale"

const getStatusBadge = (status: string) => {
  switch (status) {
    case "Success":
      return <Badge className="bg-green-100 text-green-800 hover:bg-green-100">Thành công</Badge>
    case "Failed":
      return <Badge className="bg-red-100 text-red-800 hover:bg-red-100">Thất bại</Badge>
    case "Pending":
      return <Badge className="bg-yellow-100 text-yellow-800 hover:bg-yellow-100">Đang xử lý</Badge>
    default:
      return <Badge variant="secondary">{status}</Badge>
  }
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount)
}

const formatDate = (dateString: string) => {
  return format(new Date(dateString), "dd/MM/yyyy", { locale: vi })
}

export default function TransactionHistory() {
  const [transactions, setTransactions] = useState<any[]>([])
  const [filterTransactionId, setFilterTransactionId] = useState("");
  const [filteredTransactions, setFilteredTransactions] = useState<any[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [userFilter, setUserFilter] = useState("")
  const [sortBy, setSortBy] = useState("date")
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("desc")
  const [selectedTransaction, setSelectedTransaction] = useState<any>(null)
  const [dateFrom, setDateFrom] = useState<Date>()
  const [dateTo, setDateTo] = useState<Date>()

  // Statistics calculations
  const totalTransactions = transactions.length
  const totalAmount = transactions.filter((t) => t.status === "Success").reduce((sum, t) => sum + t.amount, 0)
  const successRate = Math.round((transactions.filter((t) => t.status === "Success").length / totalTransactions) * 100)
  const paymentMethods = transactions.reduce(
    (acc, t) => {
      acc[t.paymentMethod] = (acc[t.paymentMethod] || 0) + 1
      return acc
    },
    {} as Record<string, number>,
  )

  useEffect(() => {
    fetch("http://localhost:8080/api/user/transactions", { credentials: "include" })
        .then((res) => res.json())
        .then((data) => {
          setTransactions(data)
          setFilteredTransactions(data) // 👈 phải cập nhật luôn filteredTransactions
        })
        .catch((err) => console.error("Lỗi tải giao dịch:", err))
  }, [])




  // Filter and search logic
  const applyFilters = () => {
    let filtered = [...transactions]

    if (filterTransactionId.trim() !== "") {
      filtered = filtered.filter((transaction) =>
          (transaction.transactionId || "")
              .toLowerCase()
              .includes(filterTransactionId.toLowerCase())
      );
    }
    // Search filter
    if (searchTerm) {
      filtered = filtered.filter((t) =>
          t.transactionId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          t.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
          t.amount.toString().includes(searchTerm),
      )
    }

    // Status filter
    if (statusFilter !== "all") {
      filtered = filtered.filter((t) => t.status === statusFilter)
    }

    // User email filter
    if (userFilter) {
      filtered = filtered.filter((t) => t.email.toLowerCase().includes(userFilter.toLowerCase()))
    }

    // Date range filter
    if (dateFrom) {
      filtered = filtered.filter((t) => new Date(t.createdAt) >= dateFrom)
    }
    if (dateTo) {
      filtered = filtered.filter((t) => new Date(t.createdAt) <= dateTo)
    }

    // Sort
    filtered.sort((a, b) => {
      let aValue, bValue

      switch (sortBy) {
        case "date":
          aValue = new Date(a.createdAt).getTime()
          bValue = new Date(b.createdAt).getTime()
          break
        case "amount":
          aValue = a.amount
          bValue = b.amount
          break
        default:
          aValue = a.createdAt
          bValue = b.createdAt
      }

      if (sortOrder === "asc") {
        return aValue > bValue ? 1 : -1
      } else {
        return aValue < bValue ? 1 : -1
      }
    })

    setFilteredTransactions(filtered)
  }

  // Apply filters whenever dependencies change
  useEffect(() => {
    applyFilters()
  }, [transactions, searchTerm, userFilter])


  const handleSort = (field: string) => {
    if (sortBy === field) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc")
    } else {
      setSortBy(field)
      setSortOrder("desc")
    }
    applyFilters()
  }


  const exportToPDF = () => {
    // Mock export functionality
    alert("Xuất PDF thành công!")
  }

  const printTransaction = (transaction: any) => {
    // Mock print functionality
    alert(`In phiếu giao dịch ${transaction.transactionId}`)
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Lịch sử giao dịch</h1>
          <p className="text-muted-foreground">Quản lý và theo dõi tất cả giao dịch</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={exportToPDF} variant="outline">
            <Download className="w-4 h-4 mr-2" />
            Xuất PDF
          </Button>
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="border-l-4 border-l-green-700">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng giao dịch</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalTransactions}</div>
            <p className="text-xs text-muted-foreground">giao dịch</p>
          </CardContent>
        </Card>

        <Card className="border-l-4 border-l-green-700">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tổng doanh thu</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(totalAmount)}</div>
            <p className="text-xs text-muted-foreground">từ giao dịch thành công</p>
          </CardContent>
        </Card>

        <Card className="border-l-4 border-l-green-700">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Tỷ lệ thành công</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{successRate}%</div>
            <p className="text-xs text-muted-foreground">giao dịch thành công</p>
          </CardContent>
        </Card>

        <Card className="border-l-4 border-l-green-700">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Phương thức phổ biến</CardTitle>
            <CreditCard className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {Object.entries(paymentMethods).sort(([, a], [, b]) => b - a)[0]?.[0] || "N/A"}
            </div>
            <p className="text-xs text-muted-foreground">
              {Object.entries(paymentMethods).sort(([, a], [, b]) => b - a)[0]?.[1] || 0} giao dịch
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Filter className="w-5 h-5" />
            Bộ lọc và tìm kiếm
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Search */}
            <div className="space-y-2">
              <Label>Tìm kiếm</Label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Mã GD, tên người dùng, số tiền..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {/* Status Filter */}
            <div className="space-y-2">
              <Label>Trạng thái</Label>
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tất cả</SelectItem>
                  <SelectItem value="Success">Thành công</SelectItem>
                  <SelectItem value="Failed">Thất bại</SelectItem>
                  <SelectItem value="Pending">Đang xử lý</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* User Email Filter */}
            <div className="space-y-2">
              <Label>Email người dùng</Label>
              <Input placeholder="Nhập email..." value={userFilter} onChange={(e) => setUserFilter(e.target.value)} />
            </div>

            {/* Sort */}
            <div className="space-y-2">
              <Label>Sắp xếp theo</Label>
              <Select
                value={`${sortBy}-${sortOrder}`}
                onValueChange={(value) => {
                  const [field, order] = value.split("-")
                  setSortBy(field)
                  setSortOrder(order as "asc" | "desc")
                }}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="date-desc">Ngày (mới nhất)</SelectItem>
                  <SelectItem value="date-asc">Ngày (cũ nhất)</SelectItem>
                  <SelectItem value="amount-desc">Số tiền (cao nhất)</SelectItem>
                  <SelectItem value="amount-asc">Số tiền (thấp nhất)</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Date Range */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Từ ngày</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button variant="outline" className="w-full justify-start text-left font-normal bg-transparent">
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {dateFrom ? format(dateFrom, "dd/MM/yyyy", { locale: vi }) : "Chọn ngày"}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar mode="single" selected={dateFrom} onSelect={setDateFrom} initialFocus />
                </PopoverContent>
              </Popover>
            </div>

            <div className="space-y-2">
              <Label>Đến ngày</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button variant="outline" className="w-full justify-start text-left font-normal bg-transparent">
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {dateTo ? format(dateTo, "dd/MM/yyyy", { locale: vi }) : "Chọn ngày"}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar mode="single" selected={dateTo} onSelect={setDateTo} initialFocus />
                </PopoverContent>
              </Popover>
            </div>
          </div>

          <div className="flex gap-2">
            <Button onClick={applyFilters} className="bg-green-800">
              <Search className="w-4 h-4 mr-2" />
              Áp dụng bộ lọc
            </Button>
            <Button
              variant="outline"
              onClick={() => {
                setSearchTerm("")
                setStatusFilter("all")
                setUserFilter("")
                setDateFrom(undefined)
                setDateTo(undefined)
                setSortBy("date")
                setSortOrder("desc")
                setFilteredTransactions(transactions)
              }}
            >
              <RefreshCw className="w-4 h-4 mr-2" />
              Đặt lại
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Transactions Table */}
      <Card>
        <CardHeader>
          <CardTitle>Danh sách giao dịch ({filteredTransactions.length})</CardTitle>
          <CardDescription>
            Hiển thị {filteredTransactions.length} trên tổng {totalTransactions} giao dịch
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="cursor-pointer hover:bg-muted/50" onClick={() => handleSort("id")}>
                    <div className="flex items-center gap-2">
                      Mã giao dịch
                      <ArrowUpDown className="w-4 h-4" />
                    </div>
                  </TableHead>
                  <TableHead>Người thực hiện</TableHead>
                  <TableHead>Loại giao dịch</TableHead>
                  <TableHead className="cursor-pointer hover:bg-muted/50" onClick={() => handleSort("amount")}>
                    <div className="flex items-center gap-2">
                      Số tiền
                      {sortBy === "amount" &&
                        (sortOrder === "asc" ? <ArrowUp className="w-4 h-4" /> : <ArrowDown className="w-4 h-4" />)}
                    </div>
                  </TableHead>
                  <TableHead>Phương thức</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead className="cursor-pointer hover:bg-muted/50" onClick={() => handleSort("date")}>
                    <div className="flex items-center gap-2">
                      Thời gian
                      {sortBy === "date" &&
                        (sortOrder === "asc" ? <ArrowUp className="w-4 h-4" /> : <ArrowDown className="w-4 h-4" />)}
                    </div>
                  </TableHead>
                  <TableHead>Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <>
                  {filteredTransactions.map((transaction,index) => (
                      <TableRow key={index} className="hover:bg-muted/50">
                        <TableCell className="font-medium">{transaction.transactionId}</TableCell>
                        <TableCell>
                          <div>
                            <div className="text-sm text-muted-foreground">{transaction.email}</div>
                          </div>
                        </TableCell>
                        <TableCell>{transaction.type}</TableCell>
                        <TableCell className="font-medium">{formatCurrency(transaction.amount)}</TableCell>
                        <TableCell>{transaction.paymentMethod}</TableCell>
                        <TableCell>{getStatusBadge(transaction.status)as React.ReactNode}</TableCell>
                        <TableCell>{formatDate(transaction.createdAt)}</TableCell>
                        <TableCell>
                          <div className="flex gap-2">
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button variant="outline" size="sm" onClick={() => setSelectedTransaction(transaction)}>
                                  <Eye className="w-4 h-4" />
                                </Button>
                              </DialogTrigger>
                              <DialogContent className="max-w-2xl">
                                <DialogHeader>
                                  <DialogTitle>Chi tiết giao dịch {transaction.transactionId}</DialogTitle>
                                  <DialogDescription>Thông tin chi tiết về giao dịch</DialogDescription>
                                </DialogHeader>
                                {selectedTransaction && (
                                    <div className="space-y-4">
                                      <div className="grid grid-cols-2 gap-4">
                                        <div>
                                          <Label className="text-sm font-medium">Mã giao dịch</Label>
                                          <p className="text-sm">{selectedTransaction.transactionId}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Trạng thái</Label>
                                          <div className="mt-1">{getStatusBadge(selectedTransaction.status)}</div>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Người thực hiện</Label>
                                          <p className="text-xs text-muted-foreground">{selectedTransaction.email}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Số tiền</Label>
                                          <p className="text-sm font-medium">{formatCurrency(selectedTransaction.amount)}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Phương thức thanh toán</Label>
                                          <p className="text-sm">{selectedTransaction.paymentMethod}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Loại giao dịch</Label>
                                          <p className="text-sm">{selectedTransaction.type}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Thời gian tạo</Label>
                                          <p className="text-sm">{formatDate(selectedTransaction.createdAt)}</p>
                                        </div>
                                        <div>
                                          <Label className="text-sm font-medium">Thời gian hoàn tất</Label>
                                          <p className="text-sm">
                                            {selectedTransaction.completedAt
                                                ? formatDate(selectedTransaction.completedAt)
                                                : "Chưa hoàn tất"}
                                          </p>
                                        </div>
                                      </div>
                                      <div>
                                        <Label className="text-sm font-medium">Thông điệp phản hồi</Label>
                                        <p className="text-sm bg-muted p-3 rounded-md mt-1">{selectedTransaction.message}</p>
                                      </div>
                                      <div className="flex gap-2 pt-4">
                                        <Button onClick={() => printTransaction(selectedTransaction)}>
                                          <Printer className="w-4 h-4 mr-2" />
                                          In phiếu
                                        </Button>
                                      </div>
                                    </div>
                                )}
                              </DialogContent>
                            </Dialog>
                          </div>
                        </TableCell>
                      </TableRow>
                  ))}
                </>
              </TableBody>
            </Table>
          </div>
          <>
            {filteredTransactions.length === 0 && (
                <div className="text-center py-8 text-muted-foreground">
                  Không tìm thấy giao dịch nào phù hợp với bộ lọc
                </div>
            )}
          </>
        </CardContent>
      </Card>
    </div>
  )
}
