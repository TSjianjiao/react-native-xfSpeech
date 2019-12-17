# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


- Added 新添加的功能。
- Changed 对现有功能的变更。
- Deprecated 已经不建议使用，准备很快移除的功能。
- Removed 已经移除的功能。
- Fixed 对bug的修复
- Security 对安全的改进


## [Unreleased]


## [1.0.1] - 2019-12-13
### Fixed
- 修复文档几处错误
- 修复几处typings错误

## [1.0.2] - 2019-12-16
### Added
- 增加播放语音的几个事件
- 增加播放暂停和恢复功能
### Changed
- 现在事件只能注册一次，重复注册的事件以最新的为准

## [1.1.0] - 2019-12-17
### Changed
- 现在复制文件使用多线程操作，不会阻塞`ui`

## [1.1.1] - 2019-12-17
### Changed
- 初始化结束时，进度不会为`1`而是`0`方便判断是否展示进度弹窗

