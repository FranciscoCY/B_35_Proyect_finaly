package nttdata.grupouno.com.operations.services.implementation;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.repositories.implementation.MovementDetailRepository;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import nttdata.grupouno.com.operations.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class MovementDetailService implements IMovementDetailService {

    @Autowired
    private MovementDetailRepository movementRepository;

    @Autowired
    private MasterAccountServices masterAccountServices;
    @Autowired
    private AccountClientService accountClientService;


    @Override
    public void createMovement(MovementDetailModel movement) {
        movementRepository.save(movement).subscribe();
    }

    @Override
    public Mono<MovementDetailModel> findById(Integer id) {
        return movementRepository.findById(id);
    }

    @Override
    public Flux<MovementDetailModel> findAllMovements() {
        return movementRepository.findAll();
    }

    @Override
    public Flux<MovementDetailModel> findByAccount(String account) {
        System.out.println("service: " +account);
        return movementRepository.findAll(Example.of(new MovementDetailModel(null,account,null,null, null,null,null,null,null)));
    }

    public Mono<Integer> countValue(){
        return movementRepository.count().map(m -> m.intValue());
    }

    @Override
    public Flux<MovementDetailModel> findByClient(String codeClient) {
        return accountClientService.findByCodeClient(codeClient)
                .flatMap(accountClientModel -> findByAccount(accountClientModel.getNumberAccount()));
    }

    public Mono<MasterAccountModel> checkBalance(String id){
        try {
            return masterAccountServices.findById(id).flatMap(m -> {
                if(m.getStatus().equals("A")) {
                    if (m.getAmount() >= m.getType().getAmountCommission()) { //procede la consulta
                        m.setAmount(m.getAmount() - m.getType().getAmountCommission());
                        countValue().map(r -> {
                            createMovement(new MovementDetailModel(r + 1, m.getNumberAccount(), null, 0.00, 'C', m.getType().getAmountCommission(), m.getCoinType(),null,null));
                            return r;
                        }).subscribe();
                        return masterAccountServices.updateAccount(m, m.getId());
                    } else {
                        System.out.println("No hay suficiente saldo para la consulta");
                        return null;
                    }
                } else {
                    System.out.println("Cuenta no activa");
                    return null;
                }
            });
        }
        catch (Exception e){
            System.out.println("Cuenta no existe");
            return null;
        }
    }

    @Override
    public Mono<MasterAccountModel> depositAmount(String id, Double amount){
        try {
            return masterAccountServices.findById(id).flatMap(m -> {
                if(m.getStatus().equals("A")) {
                    Date today = new Date();
                    return countByAccountMonthYear(m.getNumberAccount(),Util.getMonth(today),Util.getYear(today))
                            .flatMap(integer -> {
                                MovementDetailModel movement = new MovementDetailModel();
                                if (integer.compareTo(m.getType().getCountLimitOperation()) == 1
                                        || integer.compareTo(m.getType().getCountLimitOperation()) == 0){
                                    if (m.getAmount() + amount >= m.getType().getAmountCommission()) {
                                        m.setAmount(m.getAmount() + amount - m.getType().getAmountCommission());
                                        movement.setCommission(m.getType().getAmountCommission());
                                    }else {
                                        System.out.println("No hay suficiente saldo para la operación");
                                        return null;
                                    }
                                }else{
                                    m.setAmount(m.getAmount() + amount);
                                    movement.setCommission(0.0);
                                }

                                countValue().map(r -> {
                                    movement.setId(r+1);
                                    movement.setAmount(amount);
                                    movement.setNumberAccount(m.getNumberAccount());
                                    movement.setDate(Util.dateToString(today));
                                    movement.setMovementType('D');
                                    movement.setCurrency(m.getCoinType());
                                    movement.setMonth(Util.getMonth(today));
                                    movement.setYear(Util.getYear(today));
                                    createMovement(movement);
                                    return r;
                                }).subscribe();
                                return masterAccountServices.updateAccount(m, m.getId());
                            });
                } else {
                    System.out.println("Cuenta no activa");
                    return null;
                }
            });
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Cuenta no existe");
            return null;
        }
    }

    @Override
    public Mono<MasterAccountModel> withdrawAmount(String id, Double amount){
        try {
            return masterAccountServices.findById(id).flatMap(m -> {
                if(m.getStatus().equals("A")) {
                    Date today = new Date();
                    return countByAccountMonthYear(m.getNumberAccount(), Util.getMonth(today),Util.getYear(today))
                            .flatMap(integer -> {
                                MovementDetailModel movement = new MovementDetailModel();
                                if (integer.compareTo(m.getType().getCountLimitOperation()) == 1
                                        || integer.compareTo(m.getType().getCountLimitOperation()) == 0){
                                    if (m.getAmount() - amount >= m.getType().getAmountCommission()) {
                                        m.setAmount(m.getAmount() - amount - m.getType().getAmountCommission());
                                        movement.setCommission(m.getType().getAmountCommission());
                                    }else {
                                        System.out.println("No hay suficiente saldo para la operación");
                                        return null;
                                    }
                                }else{
                                    m.setAmount(m.getAmount() - amount);
                                    movement.setCommission(0.0);
                                }

                                countValue().map(r -> {
                                    movement.setId(r+1);
                                    movement.setAmount(amount);
                                    movement.setNumberAccount(m.getNumberAccount());
                                    movement.setDate(Util.dateToString(today));
                                    movement.setMovementType('R');
                                    movement.setCurrency(m.getCoinType());
                                    movement.setMonth(Util.getMonth(today));
                                    movement.setYear(Util.getYear(today));
                                    createMovement(movement);
                                    return r;
                                }).subscribe();
                                return masterAccountServices.updateAccount(m, m.getId());
                            });
                } else {
                    System.out.println("Cuenta no activa");
                    return null;
                }
            });
        }
        catch (Exception e){
            System.out.println("Cuenta no existe");
            return null;
        }
    }

    public Mono<Integer> countByAccountMonthYear(String numberAccount, String month, String year){
        return movementRepository.countByNumberAccountAndMovementTypeAndMonthAndYear(numberAccount,'R',month,year)
                .flatMap(x -> {
                    System.out.println(">>>>R>> " + x.intValue());
                    return movementRepository.countByNumberAccountAndMovementTypeAndMonthAndYear(numberAccount,'D',month,year)
                            .flatMap(y -> {
                                System.out.println(">>>>D>> " + y.intValue());
                                Integer total = y.intValue() + x.intValue();
                                return Mono.just(total);
                            });
                });
    }

    @Override
    public Mono<MasterAccountModel> chargeMaintenace(String numberAccount) {
        Date today = new Date();
        return movementRepository.countByNumberAccountAndMovementTypeAndMonthAndYear(numberAccount,'M',Util.getMonth(today),Util.getYear(today))
                .flatMap(aLong -> {
                    if (aLong.intValue() != 0){
                        return Mono.empty();
                    }
                        return masterAccountServices.findByAccount(numberAccount).flatMap(m -> {
                            return countByAccountMonthYear(m.getNumberAccount(),Util.getMonth(today),Util.getYear(today))
                                    .flatMap(integer -> {
                                        MovementDetailModel movement = new MovementDetailModel();
                                        if (m.getAmount() >= m.getType().getMaintenanceCommission()) {
                                            m.setAmount(m.getAmount() - m.getType().getMaintenanceCommission());
                                            movement.setCommission(0.0);
                                        }else {
                                            System.out.println("No hay suficiente saldo para el descuento");
                                            return null;
                                        }
                                        countValue().map(r -> {
                                            movement.setId(r+1);
                                            movement.setAmount(m.getType().getMaintenanceCommission());
                                            movement.setNumberAccount(m.getNumberAccount());
                                            movement.setDate(Util.dateToString(today));
                                            movement.setMovementType('M');
                                            movement.setCurrency(m.getCoinType());
                                            movement.setMonth(Util.getMonth(today));
                                            movement.setYear(Util.getYear(today));
                                            System.out.println("Se creara el cobro");
                                            createMovement(movement);
                                            return r;
                                        }).subscribe();
                                        return masterAccountServices.updateAccount(m, m.getId());
                                    });
                        });
                });
    }

}
