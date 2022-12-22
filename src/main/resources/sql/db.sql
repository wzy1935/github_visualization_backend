--create database github_visualization;
drop table if exists commit;
drop table if exists intro;
drop table if exists issue;
drop table if exists release;
drop table if exists repo;
drop table if exists wordcloud;

create table commit(
                       id serial primary key,
                       owner varchar,
                       repo varchar,
                       last_updated bigint,
                       developer_id bigint,
                       developer_name varchar,
                       commit_time bigint
);

create table issue(
                      id serial primary key,
                      owner varchar,
                      repo varchar,
                      last_updated bigint,
                      title varchar,
                      created_at bigint,
                      closed_at bigint
);

create table wordcloud(
                          id serial primary key,
                          owner varchar,
                          repo varchar,
                          last_updated bigint,
                          word varchar,
                          frequency int
);

create table release(
                        id serial primary key,
                        owner varchar,
                        repo varchar,
                        last_updated bigint,
                        name varchar,
                        published_at bigint
);

create table repo(
                           id serial primary key,
                           owner varchar,
                           repo varchar,
                           last_updated bigint
);
create table intro(
                           id serial primary key,
                           owner varchar,
                           repo varchar,
                           last_updated bigint,
                           intro varchar
);


