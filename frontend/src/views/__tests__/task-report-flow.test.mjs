import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(import.meta.dirname, '..')
const tasksViewPath = path.resolve(root, 'Tasks.vue')
const taskApiPath = path.resolve(root, '../api/task.ts')

test('任务页应提供提交巡检上报入口与审核入口', () => {
  const source = fs.readFileSync(tasksViewPath, 'utf-8')
  assert.match(source, /提交巡检上报/)
  assert.match(source, /审核上报/)
  assert.match(source, /reportDialogVisible/)
  assert.match(source, /reviewDialogVisible/)
})

test('任务 API 应提供上报提交与审核方法', () => {
  const source = fs.readFileSync(taskApiPath, 'utf-8')
  assert.match(source, /submitReport/)
  assert.match(source, /reviewReport/)
  assert.match(source, /getReportsByTask/)
  assert.match(source, /getPendingReports/)
})
