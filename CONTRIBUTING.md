# CONTRIBUTING

This Repository is the CLAON project Backend Repository of the Co-Laon team.

## 1. Fork and Clone

Please fork from `Co-Laon/claon-server` repository to your Github account,
and contribute through your forked repository.

Clone your forked repository.
```shell
git clone https://www.github.com/{your account name}/claon-server.git
```

Configure Git to sync your fork with the original repository.
```shell
git remote add upstream https://github.com/Co-Laon/claon-server.git
```

## 2. Make new branch

### Rules to manage branch

* `master`: project with release level can be merged to `master` branch
* `develop`: new feature developed after fully verified from others can be merged to `develop` branch

We recommend that make new branch in the develop branch for your contribution.
```shell
git checkout -b (branch name)
```

#### - Branch naming rule

```
(Type)/(Issue number)
```

#### - Type

```
feat: A new feature
fix: A bug fix
refac: refactored code that neither fixes a bug nor adds a feature
docs: documentation only changes
chore: extra work
style: changes that do not affect the meaning of the code
```

#### - Example

- feat/20
- refac/30

## 3. Develop and Commit

#### - Commit message rule

```
(Type): (Description)
```
- Start with lowercase letter
- Don't put a `.` at the end of the message
- Write message authoritatively

#### - Example

- fix: change type int to unit
- docs: add more description

## 4. Pull Request

Create pull request at the `Co-Laon/claon-server` repository according to the PR Template.
You can merge only when more than one team member approve.