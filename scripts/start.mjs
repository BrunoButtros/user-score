import { spawn } from 'node:child_process';
import { dirname, resolve, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { existsSync } from 'node:fs';
const root=resolve(dirname(fileURLToPath(import.meta.url)),'..');
const children=[];
const java=process.env.JAVA_HOME?join(process.env.JAVA_HOME,'bin',process.platform==='win32'?'java.exe':'java'):'java';
const modules=['score-api','user-api','web'];
for(const module of modules){const artifact=join(root,module,'target',module+(module==='web'?'.war':'.jar'));if(!existsSync(artifact)){console.error('Compile primeiro: ./mvnw verify');process.exit(1);}}
let stopping=false;
function stop(code=0){if(stopping)return;stopping=true;process.exitCode=code;for(const child of children)if(child.exitCode===null)child.kill();}
for(const module of modules){const child=spawn(java,['-Xmx256m','-jar',join(root,module,'target',module+(module==='web'?'.war':'.jar'))],{cwd:root,stdio:'inherit',windowsHide:true});children.push(child);child.on('error',e=>{console.error(e.message);stop(1);});child.on('exit',code=>{if(!stopping)stop(code||1);});}
process.on('SIGINT',()=>stop());process.on('SIGTERM',()=>stop());
console.log('User Score: http://localhost:8082 — Ctrl+C encerra os três serviços.');
