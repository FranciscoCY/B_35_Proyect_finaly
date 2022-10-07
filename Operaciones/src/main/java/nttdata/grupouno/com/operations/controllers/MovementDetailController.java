package nttdata.grupouno.com.operations.controllers;

import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.services.implementation.AccountClientService;
import nttdata.grupouno.com.operations.services.implementation.MasterAccountServices;
import nttdata.grupouno.com.operations.services.implementation.MovementDetailService;
import nttdata.grupouno.com.operations.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/operation/movement")
public class MovementDetailController {

    private static final Logger logger = LogManager.getLogger(MovementDetailController.class);

    @Autowired
    MovementDetailService movementService;
    @Autowired
    AccountClientService clientService;
    @Autowired
    MasterAccountServices masterAccountServices;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createMovement(@RequestBody MovementDetailModel movement){
        movementService.createMovement(movement);
    }

    @GetMapping(value = "/all")
    @ResponseBody

    public Flux<MovementDetailModel> findAllAccount() {
        return movementService.findAllMovements();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Mono<MovementDetailModel>> findMovementById(@PathVariable("id") Integer id){
        Mono<MovementDetailModel> accountM = movementService.findById(id);
        return new ResponseEntity<>(accountM, accountM != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/client/{codeClient}")
    public ResponseEntity<Flux<MovementDetailModel>> findMovementByClient(@PathVariable("codeClient") String codeClient){
        Flux<MovementDetailModel> accountM = movementService.findByClient(codeClient);
        return new ResponseEntity<>(accountM, accountM != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account/{account}")
    public ResponseEntity<Flux<MovementDetailModel>> findMovementByAccount(@PathVariable("account") String account){
        Flux<MovementDetailModel> fluxMov = movementService.findByAccount(account);
        return new ResponseEntity<>(fluxMov, HttpStatus.OK);
    }

    @GetMapping("/operation/check/{id}")
    public ResponseEntity<Mono<MasterAccountModel>> checkBalance(@PathVariable("id") String id){
        Mono<MasterAccountModel> masterAccount = movementService.checkBalance(id);
        return new ResponseEntity<>(masterAccount, masterAccount!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/operation/deposit/{id}/{amount}")
    public ResponseEntity<Mono<MasterAccountModel>> depositAmount(@PathVariable("id") String id, @PathVariable("amount") Double amount){
        Mono<MasterAccountModel> masterAccount = movementService.depositAmount(id,amount);
        return new ResponseEntity<>(masterAccount, masterAccount!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/operation/withdraw/{id}/{amount}")
    public ResponseEntity<Mono<MasterAccountModel>> withdrawAmount(@PathVariable("id") String id, @PathVariable("amount") Double amount){
        Mono<MasterAccountModel> masterAccount = movementService.withdrawAmount(id,amount);
        return new ResponseEntity<>(masterAccount, masterAccount!=null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/transfer")
    public Mono<ResponseEntity<Map<String, Object>>> transfer(@RequestBody Map<String, String> body) {
        String uriTransfer= "/transfer";
        String rootAccount = body.get("rootAccount");
        String destinationAccount = body.get("destinationAccount");
        Double amount = Double.parseDouble(body.get("amount"));
        MasterAccountModel rootAccount1 = new MasterAccountModel();
        MasterAccountModel destinationAccount1 = new MasterAccountModel();

        Map<String, Object> respuesta = new HashMap<>();
        Flux<MasterAccountModel> master = masterAccountServices.findAllAccount();
        Flux<MasterAccountModel> master1 = masterAccountServices.findAllAccount();
        master.filter(a -> a.getNumberAccount().equals(rootAccount)).subscribe(b -> {
            rootAccount1.setNumberAccount(b.getNumberAccount());
            rootAccount1.setId(b.getId());
            rootAccount1.setType(b.getType());
            rootAccount1.setAmount(b.getAmount());
            rootAccount1.setStatus(b.getStatus());
            rootAccount1.setCoinType(b.getCoinType());
            rootAccount1.setEndDate(b.getEndDate());
            rootAccount1.setStartDate(b.getStartDate());

                master1.filter(a -> a.getNumberAccount().equals(destinationAccount))
                        .subscribe(c -> {
                            destinationAccount1.setNumberAccount(c.getNumberAccount());
                            destinationAccount1.setId(c.getId());
                            destinationAccount1.setType(c.getType());
                            destinationAccount1.setAmount(c.getAmount());
                            destinationAccount1.setStatus(c.getStatus());
                            destinationAccount1.setCoinType(c.getCoinType());
                            destinationAccount1.setEndDate(c.getEndDate());
                            destinationAccount1.setStartDate(c.getStartDate());
                            
                            respuesta.put("Cuenta Destino", "Cuenta destino existe");
                            if(rootAccount1.getAmount() >= amount){
                                movementService.withdrawAmount(rootAccount1.getId(),amount);
                                movementService.depositAmount(destinationAccount1.getId(),amount);
                            }

                        });
            Mono.just(ResponseEntity.created(URI.create(uriTransfer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(respuesta));
        });

        return Mono.just(ResponseEntity.created(URI.create(uriTransfer))
                .contentType(MediaType.APPLICATION_JSON)
                .body(respuesta));

    }

    @GetMapping("/maintenace/{numberAccount}")
    public Mono<ResponseEntity<Map<String, Object>>> chargeMaintenace(@PathVariable("numberAccount") String numberAccount){
        Map<String, Object> response = new HashMap<>();
          return masterAccountServices.findByAccount(numberAccount).flatMap(a -> {
            if (!a.getType().getCode().equals("AHO2")){
                response.put("msg","El tipo de Cuenta no es Corriente");
                return Mono.just(ResponseEntity.badRequest().body(response));
            }
            return clientService.findByNumBerAccount(a.getNumberAccount())
                    .filter(accountClientModel -> accountClientModel.getStatus().equals("T"))
                    .take(1).next()
                    .flatMap(b -> {
                        if (b.getTypeClient().equals("J") && b.getPyme().equals(1)){
                            response.put("msg","El tipo de Cliente no aplica mantenimiento");
                            return Mono.just(ResponseEntity.badRequest().body(response));
                        }else{
                            return movementService.chargeMaintenace(a.getNumberAccount())
                                    .flatMap(d -> {
                                        response.put("msg","Se cargo el mantenimiento");
                                        response.put("account",d);
                                        return Mono.just(ResponseEntity.created(URI.create("/transfer"))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(response));
                                    }).defaultIfEmpty(ResponseEntity.badRequest().body(response));
                        }
                    });
        });
    }


    @PutMapping("/movementCardDebit")
    public Mono<ResponseEntity<Map<String, Object>>> movementCardDebit(@RequestBody Map<String, String> body) {
        String idCartClient = body.get("idCartClient");
        Double amount = Double.parseDouble(body.get("amount"));
        String movementType = body.get("movementType");

        logger.debug("idCartClient " + idCartClient);
        logger.debug("amount operation " + amount);
        logger.debug("movementType " + movementType);

        Map<String, Object> respuesta = new HashMap<>();

        Flux<AccountClientModel> accountCard = clientService.findByidCartClient(idCartClient);
        accountCard.filter(a -> a.getPrincipalAccount().equals("S")).subscribe(b -> {
            // validamos si superó la cantidad de transacciones para considerar la comisión
            MovementDetailModel movement = new MovementDetailModel();
            masterAccountServices.validAccountBalance(b.getNumberAccount(),amount, movementType.charAt(0)).subscribe(m -> {
                Date today = new Date();
                if(m.getBlSaldo()){
                    // se realiza la transacción
                    movementService.countValue().map(r -> {
                        movement.setId(r+1);
                        movement.setAmount(amount);
                        movement.setNumberAccount(m.getAccountModel().getNumberAccount());
                        movement.setDate(Util.dateTimeToString(today));
                        movement.setMovementType(movementType.charAt(0));
                        movement.setCurrency(m.getAccountModel().getCoinType());
                        movement.setMonth(Util.getMonth(today));
                        movement.setYear(Util.getYear(today));
                        createMovement(movement);
                        masterAccountServices.updateAccount(m.getAccountModel(), m.getAccountModel().getId()).subscribe();
                        return r;
                    }).subscribe();
                    logger.debug("exito");
                    respuesta.put("mensaje","Se realiza el movimiento con la cuenta " + b.getNumberAccount());
                }
                else{
                    logger.debug("la cuenta " + b.getNumberAccount() + " no tiene saldo suficiente");
                    accountCard.sort( (obj1, obj2) -> Util.stringToDateTime(obj1.getOpeningDate()).compareTo(Util.stringToDateTime(obj2.getOpeningDate()))).subscribe(c -> {
                        masterAccountServices.validAccountBalance(c.getNumberAccount(),amount,movementType.charAt(0)).subscribe(m2 -> {
                            if(m2.getBlSaldo()){
                                // se realiza la transacción
                                movementService.countValue().map(r -> {
                                    movement.setId(r+1);
                                    movement.setAmount(amount);
                                    movement.setNumberAccount(m2.getAccountModel().getNumberAccount());
                                    movement.setDate(Util.dateToString(today));
                                    movement.setMovementType(movementType.charAt(0));
                                    movement.setCurrency(m2.getAccountModel().getCoinType());
                                    movement.setMonth(Util.getMonth(today));
                                    movement.setYear(Util.getYear(today));
                                    createMovement(movement);
                                    masterAccountServices.updateAccount(m2.getAccountModel(), m2.getAccountModel().getId()).subscribe();
                                    return r;
                                }).subscribe();
                                logger.debug("exito");
                                respuesta.put("mensaje","Se realiza el movimiento con la cuenta " + c.getNumberAccount());
                            }
                            else{
                                logger.debug("la cuenta " + m2.getAccountModel().getNumberAccount() + " no tiene saldo suficiente");
                            }
                        });

                    });
                }
            });
        });

        return Mono.just(ResponseEntity.created(URI.create("/movementCardDebit"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(respuesta));

    }
}
