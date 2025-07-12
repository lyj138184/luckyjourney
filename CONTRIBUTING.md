# 为幸运日做贡献

欢迎来到幸运日，这里是为你准备的贡献指南列表。如果您发现页面上有不正确或缺失的内容，请提交问题或 PR 进行修复。


## 你能做些什么
鼓励采取一切行动来使项目变得更好。在 GitHub 上，项目的每一项改进都可以通过 PR（pull request 的缩写）进行。


* 如果您发现错别字，请尝试修复它！
* 如果您发现错误，请尝试修复它！
* 如果您发现一些多余的代码，请尝试删除它们！
* 如果您发现缺少某些测试用例，请尝试添加它们！
* 如果您可以增强功能，请不要犹豫！
* 如果您发现代码是隐式的，请尝试添加注释以使其清晰！
* 如果你觉得代码很丑陋，试着重构它！
* 如果你能帮助改进文件，那就再好不过了！
* 如果您发现文档不正确，请执行并修复它！
* ...




## 贡献
### 设备
在贡献之前，您需要注册一个 Github ID。 准备以下环境：
* JDK 1.8
* git

### Workflow

1. Fork 到你的仓库
2. 将 fork 克隆到本地存储库
3. 创建一个新分支并对其进行处理
4. 让您的分支保持同步
5. 提交更改（确保提交消息简洁）
6. 将提交推送到分叉存储库
7. 创建PR

请遵循拉取请求模板。请确保 PR 有相应的问题。
创建 PR 后，将向拉取请求分配一个或多个审阅者。审阅者将审阅代码。
在合并 PR 之前，请删除任何修复审查反馈、拼写错误、合并和变基的提交。最终的提交信息应该简洁明了



### 提交规则
#### 提交消息

提交消息可以帮助审阅者更好地理解提交 PR 的目的是什么。它还可以帮助加快代码审查过程。我们鼓励贡献者使用 EXPLICIT 提交消息，而不是模棱两可的消息。一般来说，我们提倡以下提交消息类型：

* feat: 新功能
* fix: 修复错误
* docs: 更改文档
* style: 不影响代码含义的更改（空格、格式、缺少分号等）
* refactor:重构：既不修复错误也不添加功能的代码更改
* perf: 提高性能的代码更改
* test: 添加缺失的测试或更正现有测试
* chore: 对构建过程或辅助工具和库（如文档生成）的更改(杂活：7788)

另一方面，我们不鼓励贡献者通过以下方式提交消息：

* ~~fix bug 修复bug~~
* ~~update 更新~~
* ~~add doc 添加文档~~




#### 提交内容

提交内容表示一次提交中包含的所有内容更改。我们最好将内容包含在一个提交中，这样可以支持审阅者的完整审查，而无需任何其他提交的帮助。换句话说，单个提交中的内容可以通过 CI 以避免代码混乱。简而言之，我们需要牢记两条小规则：

* 避免在提交中发生非常大的更改;
* 每次提交都完整且可查看。

无论是提交消息还是提交内容，我们确实更重视代码审查。


### Pull Request

请注意，单个 PR 不应太大。如果需要进行大量更改，最好将更改分隔到几个单独的 PR 中。

### Code Review
所有代码都应由一个或多个提交者进行仔细审查。一些原则：

- 可读性：重要的代码应该有很好的文档记录。遵守我们的代码风格。
- 优雅：新的函数、类或组件应该设计得很好。
- 可测试性：重要代码应经过充分测试（高单元测试覆盖率）。



### 为您的作品签名
签字是补丁解释末尾的一行简单，证明您编写了它或有权将其作为开源补丁传递。规则非常简单：如果您能证明以下内容 (from [developercertificate.org](http://developercertificate.org/)):

```
Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.
660 York Street, Suite 102,
San Francisco, CA 94110 USA

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.

Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
```

然后，您只需在每条git commit消息中添加一行：

```
Signed-off-by: Joe Smith <joe.smith@email.com>
```

使用您的真实姓名（对不起，没有化名或匿名贡献。

如果设置了 和 user.name user.email git 配置，则可以使用 git commit -s 自动对提交进行签名。