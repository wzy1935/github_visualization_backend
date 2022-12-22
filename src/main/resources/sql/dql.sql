#1
select count(id)
from commit
where owner = 'asmjit'
  and repo = 'asmjit';

#2
select count(developer_id) cnt, developer_name, developer_id
from commit
where owner = 'asmjit'
  and repo = 'asmjit'
group by developer_id, developer_name
order by count(developer_id) desc
limit 10;

#3
select (select count(id)
        from issue
        where owner = 'asmjit'
          and repo = 'asmjit'
          and closed_at is null)     open_cnt,
       (select count(id)
        from issue
        where owner = 'asmjit'
          and repo = 'asmjit'
          and closed_at is not null) closed_cnt,
       (select count(id)
        from issue
        where owner = 'asmjit'
          and repo = 'asmjit') total_cnt;

#4
(select created_at open
 from issue
 where owner = 'asmjit'
   and repo = 'asmjit'
   and closed_at is null
   and created_at < 1470266909000
   and created_at > 1393211185000
);

(select created_at closed
 from issue
 where owner = 'asmjit'
   and repo = 'asmjit'
   and closed_at is not null
   and created_at < 1470266909000
   and created_at > 1393211185000
);

(select created_at total
 from issue
 where owner = 'asmjit'
   and repo = 'asmjit'
   and created_at < 1470266909000
   and created_at > 1393211185000
);

#5
(select (closed_at - created_at) time_cost
 from issue
 where owner = 'asmjit'
   and repo = 'asmjit'
   and closed_at is not null
);

#7
(select count(id)
 from release
 where owner = 'facebook'
   and repo = 'react');

#8
(select name, published_at
 from release
 where owner = 'facebook'
   and repo = 'react');

(select count(id)
 from commit
 where owner = 'facebook'
   and repo = 'react'
   and commit_time > 1651007700000
   and commit_time < 1655236461000);


#9
(select commit_time
 from commit
 where owner = 'facebook'
   and repo = 'react')