package com.example.expensetracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter


enum class ExpenseCategory {
    FOOD, TRANSPORT, HOUSING, ENTERTAINMENT, UTILITIES, HEALTH, OTHER
}


data class Expense(
    val id: Int,
    val description: String,
    val amount: Double,
    val date: LocalDate,
    val category: ExpenseCategory
)


class ExpenseTracker {
    private val expenses = mutableListOf<Expense>()
    private var nextId = 1

 
    fun addExpense(description: String, amount: Double, date: LocalDate = LocalDate.now(), category: ExpenseCategory = ExpenseCategory.OTHER): Expense {
        val expense = Expense(nextId++, description, amount, date, category)
        expenses.add(expense)
        return expense
    }

    fun removeExpense(id: Int): Boolean {
        return expenses.removeIf { it.id == id }
    }

   
    fun getTotalSpending(): Double {
        return expenses.sumOf { it.amount }
    }

    
    fun getSpendingByCategory(): Map<ExpenseCategory, Double> {
        return expenses.groupBy { it.category }
            .mapValues { (_, categoryExpenses) -> categoryExpenses.sumOf { it.amount } }
    }

 
    fun getExpensesInRange(startDate: LocalDate, endDate: LocalDate): List<Expense> {
        return expenses.filter { it.date in startDate..endDate }
    }

   
    fun viewExpenseList() {
        println("\n--- Detailed Expense List ---")
        if (expenses.isEmpty()) {
            println("No records found.")
            return
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        println("ID  | Date       | Category      | Description          | Amount")
        println("----------------------------------------------------------------")
        expenses.sortedByDescending { it.date }.forEach {
            println("${it.id.toString().padEnd(3)} | ${it.date.format(formatter)} | ${it.category.name.padEnd(13)} | ${it.description.padEnd(20)} | $${"%.2f".format(it.amount)}")
        }
    }

 
    fun viewExpensesByCategory(category: ExpenseCategory) {
        val filtered = expenses.filter { it.category == category }
        println("\n--- Expenses for Category: ${category.name} ---")
        if (filtered.isEmpty()) {
            println("No expenses found in this category.")
            return
        }
        filtered.forEach {
            println("${it.date}: ${it.description} - $${"%.2f".format(it.amount)}")
        }
        println("Subtotal: $${"%.2f".format(filtered.sumOf { it.amount })}")
    }

  
    fun displaySummary() {
        println("\n================ EXPENSE SUMMARY ================")
        if (expenses.isEmpty()) {
            println("No data available.")
            return
        }

        println("Total Records: ${expenses.size}")
        println("Total Spending: $${"%.2f".format(getTotalSpending())}")
        
        println("\nSpending Breakdown:")
        getSpendingByCategory().forEach { (category, amount) ->
            println("- ${category.name.padEnd(13)}: $${"%.2f".format(amount)} (${"%.1f".format((amount/getTotalSpending())*100)}%)")
        }
        println("================================================")
    }
}


fun main() {
    val tracker = ExpenseTracker()
    var running = true

    println("╔══════════════════════════════════════════╗")
    println("║      DAILY EXPENSE TRACKER PRO v1.0      ║")
    println("╚══════════════════════════════════════════╝")

    while (running) {
        println("\nMain Menu:")
        println("1.  Add New Expense")
        println("2.  View Expense List")
        println("3.  View by Category")
        println("4.  Show Total Spending")
        println("5.  General Summary")
        println("6.  Exit")
        print("Selection > ")

        when (readLine()?.trim()) {
            "1" -> {
                println("\nEnter Details:")
                print("Description : ")
                val desc = readLine() ?: ""
                print("Amount      : ")
                val amount = readLine()?.toDoubleOrNull() ?: 0.0
                println("Categories  :")
                ExpenseCategory.values().forEachIndexed { index, cat -> println("  ${index + 1}. ${cat.name}") }
                print("Choose (1-${ExpenseCategory.values().size}): ")
                val catIndex = (readLine()?.toIntOrNull() ?: 1) - 1
                val category = ExpenseCategory.values().getOrElse(catIndex) { ExpenseCategory.OTHER }
                
                tracker.addExpense(desc, amount, category = category)
                println("✅ Expense recorded!")
            }
            "2" -> tracker.viewExpenseList()
            "3" -> {
                println("\nSelect Category to Filter:")
                ExpenseCategory.values().forEachIndexed { index, cat -> println("${index + 1}. ${cat.name}") }
                print("Category # > ")
                val catIndex = (readLine()?.toIntOrNull() ?: 1) - 1
                val category = ExpenseCategory.values().getOrElse(catIndex) { null }
                if (category != null) {
                    tracker.viewExpensesByCategory(category)
                } else {
                    println("Invalid selection.")
                }
            }
            "4" -> {
                println("\n-------------------------------------------")
                println("NET SPENDING: $${"%.2f".format(tracker.getTotalSpending())}")
                println("-------------------------------------------")
            }
            "5" -> tracker.displaySummary()
            "6" -> {
                println("\nThank you for using Expense Tracker Pro. Goodbye!")
                running = false
            }
            else -> println(" Invalid option. Please select 1-6.")
        }
    }
}
