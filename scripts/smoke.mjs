import assert from 'node:assert/strict';
import { spawn } from 'node:child_process';
import { createServer } from 'node:http';
import { createServer as createNetServer } from 'node:net';
import { mkdtempSync, openSync, closeSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, resolve, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root=resolve(dirname(fileURLToPath(import.meta.url)),'..');
const runDir=mkdtempSync(join(tmpdir(),'userscore-smoke-'));
const processes=[];
const java=process.env.JAVA_HOME ? join(process.env.JAVA_HOME,'bin',process.platform==='win32'?'java.exe':'java') : 'java';
async function freePort(){const s=createNetServer();await new Promise(r=>s.listen(0,'127.0.0.1',r));const p=s.address().port;await new Promise(r=>s.close(r));return p;}
async function wait(url,child){const deadline=Date.now()+90000;while(Date.now()<deadline){if(child&&(child.spawnFailed||child.closed||child.exitCode!==null||child.signalCode!==null))throw Error('Application exited: '+url);try{const r=await fetch(url,{signal:AbortSignal.timeout(2000)});if(r.ok)return;}catch{}await new Promise(r=>setTimeout(r,500));}throw Error('Startup timed out: '+url);}
function start(module,port,env={}){const log=join(runDir,module+'.log');const fd=openSync(log,'w');const child=spawn(java,['-Xmx256m','-jar',join(root,module,'target',module+(module==='web'?'.war':'.jar'))],{cwd:runDir,env:{...process.env,SERVER_PORT:String(port),...env},stdio:['ignore',fd,fd],windowsHide:true});closeSync(fd);child.on('error',e=>{child.spawnFailed=true;console.error(e.message);});child.on('close',()=>{child.closed=true;});processes.push({child,log});return child;}
async function call(base,url,method='GET',data,token){const r=await fetch(base+url,{method,headers:{...(data?{'Content-Type':'application/json'}:{}),...(token?{Authorization:'Bearer '+token}:{})},body:data?JSON.stringify(data):undefined,redirect:'manual'});const txt=await r.text();let body;try{body=JSON.parse(txt);}catch{body=txt;}return {status:r.status,body,headers:r.headers};}
let fixture;
try{
 const [sp,up,wp]=await Promise.all([freePort(),freePort(),freePort()]);
 fixture=createServer((req,res)=>{res.setHeader('Content-Type','application/json');res.end(JSON.stringify(req.url==='/01001000/json/'?{cep:'01001-000',logradouro:'Praça da Sé',bairro:'Sé',localidade:'São Paulo',uf:'SP',ibge:'3550308'}:{erro:true}));});
 await new Promise(r=>fixture.listen(0,'127.0.0.1',r));
 const running=process.argv.includes('--running');
 const score=running?'http://127.0.0.1:8081':`http://127.0.0.1:${sp}`,api=running?'http://127.0.0.1:8080':`http://127.0.0.1:${up}`,web=running?'http://127.0.0.1:8082':`http://127.0.0.1:${wp}`;
 const sc=running?null:start('score-api',sp);await wait(score+'/actuator/health',sc);
 const mysql=process.argv.includes('--mysql');
 if(mysql&&!process.env.SMOKE_MYSQL_URL)throw Error('Defina SMOKE_MYSQL_URL apontando para um banco MySQL vazio de teste.');
 const uc=running?null:start('user-api',up,{SPRING_PROFILES_ACTIVE:mysql?'mysql':'local',SPRING_DATASOURCE_URL:mysql?process.env.SMOKE_MYSQL_URL:'jdbc:h2:mem:smoke;MODE=MySQL;DB_CLOSE_DELAY=-1',SCORE_API_URL:score+'/score',VIACEP_URL:process.argv.includes('--live-cep')?'https://viacep.com.br/ws/':`http://127.0.0.1:${fixture.address().port}/`,REPORTS_SCHEDULE:'3600000',ENVIAR_EMAIL:'false'});await wait(api+'/actuator/health',uc);
 const wc=running?null:start('web',wp,{USER_SCORE_BASE_URL:api});await wait(web+'/actuator/health',wc);
 const name=`O'Brien </script><script>alert(1)</script>`;
 const suffix=String(Date.now()).slice(-9);
 const registration={login:'smoke-'+suffix,password:'smoke-password-2026',email:'smoke@example.test',nome:name,cpf:'10'+suffix,telefone:'11999999999',cep:'01001000',role:'ADMIN'};
 const registered=await call(api,'/user/cadastrar','POST',registration);assert.equal(registered.status,201,JSON.stringify(registered.body));const id=registered.body.id;
 assert.equal(registered.body.endereco.localidade,'São Paulo');assert.ok(registered.body.score>=0&&registered.body.score<=100);
 const second=await call(api,'/user/cadastrar','POST',{...registration,login:'second-'+suffix,cpf:'20'+suffix});assert.equal(second.status,201);
 assert.equal((await call(api,'/user/cadastrar','POST',registration)).status,400);
 assert.equal((await call(api,'/user/cadastrar','POST',{...registration,login:'bad-cep-'+suffix,cpf:'30'+suffix,cep:'00000000'})).status,400);
 assert.equal((await call(api,'/user/cadastrar','POST',{login:'invalid'})).status,400);
 assert.equal((await call(api,'/auth/login','POST',{login:registration.login,password:'incorrect'})).status,401);
 const login=await call(api,'/auth/login','POST',{login:registration.login,password:registration.password});assert.equal(login.status,200);const token=login.body.token;assert.ok(token);
 assert.equal((await call(api,'/user/scores')).status,401);
 assert.equal((await call(api,'/user/scores','GET',undefined,'invalid')).status,401);
 const scores=await call(api,'/user/scores','GET',undefined,token);assert.equal(scores.status,200);assert.ok(scores.body.some(u=>u.nome===name));
 assert.equal((await call(api,`/user/${id}/score`,'GET',undefined,token)).status,200);
 assert.equal((await call(api,`/user/${id}`,'DELETE',undefined,token)).status,403,'Public role ADMIN must not elevate privileges');
 assert.equal((await call(api,`/user/${id}`,'PUT',{email:'changed@example.test'},token)).status,403);
 assert.equal((await call(api,'/user/buscar','GET',undefined,token)).status,403);
 assert.equal((await call(web,'/lista')).status,302);
 const page=await call(web,'/login');assert.equal(page.status,200);assert.ok(page.body.includes('User Score'));
 const cookie=page.headers.get('set-cookie').split(';')[0];const csrf=page.body.match(/name="_csrf" value="([^"]+)"/)[1];
 const noCsrf=await fetch(web+'/login',{method:'POST',headers:{Cookie:cookie},body:new URLSearchParams({login:registration.login,password:registration.password}),redirect:'manual'});assert.equal(noCsrf.status,403);
 const posted=await fetch(web+'/login',{method:'POST',headers:{Cookie:cookie},body:new URLSearchParams({login:registration.login,password:registration.password,_csrf:csrf}),redirect:'manual'});assert.equal(posted.status,302);assert.equal(new URL(posted.headers.get('location'),web).href,web+'/lista');
 const authenticatedCookie=posted.headers.get('set-cookie').split(';')[0];assert.notEqual(authenticatedCookie,cookie,'Session ID must rotate at login');
 const list=await fetch(web+'/lista',{headers:{Cookie:authenticatedCookie}});assert.equal(list.status,200);const html=await list.text();assert.ok(html.includes('O&#039;Brien')||html.includes('O&#39;Brien'));assert.ok(html.includes('&lt;/script&gt;'));assert.ok(!html.includes('<script>alert(1)</script>'));
 const logoutCsrf=html.match(/name="_csrf" value="([^"]+)"/)[1];
 const logout=await fetch(web+'/logout',{method:'POST',headers:{Cookie:authenticatedCookie},body:new URLSearchParams({_csrf:logoutCsrf}),redirect:'manual'});assert.equal(logout.status,302);
 const after=await fetch(web+'/lista',{headers:{Cookie:authenticatedCookie},redirect:'manual'});assert.equal(after.status,302);
 const report={status:'PASS',scenario:'auth-and-three-services',database:running?'Docker/MySQL':mysql?'MySQL':'H2',externalCep:running||process.argv.includes('--live-cep')?'ViaCEP live':'controlled HTTP fixture',logs:runDir};
 console.log(JSON.stringify(report,null,2));
 if(process.env.SMOKE_REPORT)writeFileSync(process.env.SMOKE_REPORT,JSON.stringify(report,null,2));
}catch(error){console.error(error);for(const {log} of processes){console.error(log);console.error(readFileSync(log,'utf8').slice(-6000));}process.exitCode=1;
}finally{
 await Promise.all(processes.map(({child})=>new Promise(r=>{if(child.closed||child.spawnFailed||child.exitCode!==null||child.signalCode!==null)return r();const timer=setTimeout(()=>{if(!child.closed){child.kill('SIGKILL');process.exitCode=1;console.error('Cleanup exceeded deadline for PID '+child.pid);}r();},5000);child.once('close',()=>{clearTimeout(timer);r();});child.kill();})));
 if(fixture)await new Promise(r=>fixture.close(r));
}
